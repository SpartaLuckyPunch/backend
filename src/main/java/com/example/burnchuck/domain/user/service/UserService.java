package com.example.burnchuck.domain.user.service;

import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.Review;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.utils.JwtUtil;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.meetingLike.repository.MeetingLikeRepository;
import com.example.burnchuck.domain.review.repository.ReviewRepository;
import com.example.burnchuck.domain.user.dto.request.*;
import com.example.burnchuck.domain.user.dto.response.*;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final FollowRepository followRepository;
    private final MeetingLikeRepository meetingLikeRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    /**
     * 내 정보 수정(닉네임, 주소)
     * <p>
     * 고도화 작업 시, 프로필 이미지 수정 항목 추가 예정
     */
    @Transactional
    public UserUpdateProfileResponse updateProfile(AuthUser authUser, UserUpdateProfileRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        String currentNickname = user.getNickname();
        String newNickname = request.getNickname();

        boolean isNicknameChanged = !Objects.equals(currentNickname, newNickname);
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

        if (Objects.equals(oldPassword, newPassword)) {
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

        user.delete();
        userRepository.saveAndFlush(user);

        meetingLikeRepository.deleteByUserId(user.getId());

        followRepository.deleteByFollowerId(user.getId());
        followRepository.deleteByFolloweeId(user.getId());
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
}