package com.example.burnchuck.domain.user.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.GetS3Url;
import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.Review;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.S3UrlGenerator;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.meeting.service.AttendanceService;
import com.example.burnchuck.domain.meeting.service.MeetingService;
import com.example.burnchuck.domain.meetingLike.repository.MeetingLikeRepository;
import com.example.burnchuck.domain.notification.service.EmitterService;
import com.example.burnchuck.domain.review.repository.ReviewRepository;
import com.example.burnchuck.domain.user.dto.request.UserUpdatePasswordRequest;
import com.example.burnchuck.domain.user.dto.request.UserUpdateProfileRequest;
import com.example.burnchuck.domain.user.dto.response.UserGetAddressResponse;
import com.example.burnchuck.domain.user.dto.response.UserGetProfileReponse;
import com.example.burnchuck.domain.user.dto.response.UserUpdateProfileResponse;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final FollowRepository followRepository;
    private final MeetingLikeRepository meetingLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final MeetingRepository meetingRepository;

    private final MeetingService meetingService;
    private final AttendanceService attendanceService;
    private final EmitterService emitterService;

    private final PasswordEncoder passwordEncoder;
    private final S3UrlGenerator s3UrlGenerator;

    /**
     * 프로필 이미지 업로드 Presigned URL 생성
     */
    public GetS3Url getUploadProfileImgUrl(AuthUser authUser, String filename) {

        String key = "profile/" + authUser.getId() + "/" + UUID.randomUUID();
        return s3UrlGenerator.generateUploadImgUrl(filename, key);
    }

    /**
     * 프로필 이미지 등록
     */
    public GetS3Url getViewProfileImgUrl(AuthUser authUser, String key) {

        s3UrlGenerator.validateKeyOwnership(authUser.getId(), key);

        if (!s3UrlGenerator.isFileExists(key)) {
            throw new CustomException(ErrorCode.USER_IMG_NOT_FOUND);
        }

        User user = userRepository.findActivateUserById(authUser.getId());

        GetS3Url result = s3UrlGenerator.generateViewImgUrl(key);

        user.uploadProfileImg(result.getPreSignedUrl());
        userRepository.saveAndFlush(user);

        return result;
    }

    /**
     * 내 정보 수정(닉네임, 주소)
     * 고도화 작업 시, 프로필 이미지 수정 항목 추가 예정
     */
    @Transactional
    public UserUpdateProfileResponse updateProfile(AuthUser authUser, UserUpdateProfileRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        String currentNickname = user.getNickname();
        String newNickname = request.getNickname();

        boolean isNicknameChanged = !ObjectUtils.nullSafeEquals(currentNickname, newNickname);
        boolean existNickname = userRepository.existsByNickname(newNickname);

        if (isNicknameChanged && existNickname) {
            throw new CustomException(ErrorCode.NICKNAME_EXIST);
        }

        Address newAddress = addressRepository.findAddressByAddressInfo(
            request.getProvince(),
            request.getCity(),
            request.getDistrict()
        );

        user.updateProfile(newNickname, newAddress);
        userRepository.saveAndFlush(user);

        return UserUpdateProfileResponse.from(user, newAddress);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(AuthUser authUser, UserUpdatePasswordRequest request) {

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        if (ObjectUtils.nullSafeEquals(oldPassword, newPassword)) {
            throw new CustomException(ErrorCode.SAME_PASSWORD);
        }

        User user = userRepository.findActivateUserById(authUser.getId());

        boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());

        if (!matches) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.updatePassword(encodedPassword);
        userRepository.saveAndFlush(user);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteUser(AuthUser authUser) {

        User user = userRepository.findActivateUserById(authUser.getId());

        cancelAttendanceMeetings(authUser, user);
        cancelHostedMeetings(authUser);

        meetingLikeRepository.deleteByUserId(user.getId());

        followRepository.deleteByFollowerId(user.getId());
        followRepository.deleteByFolloweeId(user.getId());

        emitterService.disconnectAllEmittersByUserId(user.getId());

        user.delete();
        userRepository.saveAndFlush(user);
    }

    /**
     * 참가 신청한 모임 중 COMPLETED 되지 않은 모임 참가 취소 처리
     */
    public void cancelAttendanceMeetings(AuthUser authUser, User user) {

        List<Meeting> attendanceMeetingList = userMeetingRepository.findActiveMeetingsByUser(user);

        for (Meeting meeting : attendanceMeetingList) {

            attendanceService.cancelAttendance(authUser, meeting.getId());
        }
    }

    /**
     * 주최한 모임 중 COMPLETED 되지 않은 모임 취소 처리
     */
    public void cancelHostedMeetings(AuthUser authUser) {

        List<Meeting> hostedMeetingList = meetingRepository.findActiveHostedMeetings(authUser.getId());

        for (Meeting meeting : hostedMeetingList) {

            meetingService.deleteMeeting(authUser, meeting.getId());
        }
    }

    /**
     * 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserGetProfileReponse getProfile(Long userId) {

        User user = userRepository.findActivateUserById(userId);

        Long followings = followRepository.countByFollower(user);
        Long followers = followRepository.countByFollowee(user);

        List<Review> reviewList = reviewRepository.findAllByReviewee(user);

        double avgRates = reviewList.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);

        return new UserGetProfileReponse(
            user.getProfileImgUrl(),
            user.getNickname(),
            followings,
            followers,
            avgRates
        );
    }

    /**
     * 주소 조회
     */
    @Transactional(readOnly = true)
    public UserGetAddressResponse getAddress(AuthUser authUser) {

        User user = userRepository.findActivateUserById(authUser.getId());

        return UserGetAddressResponse.from(user.getAddress());
    }
}
