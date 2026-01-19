package com.example.burnchuck.domain.meetingLike.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingLike;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meetingLike.model.response.MeetingLikeCountResponse;
import com.example.burnchuck.domain.meetingLike.model.response.MeetingLikeResponse;
import com.example.burnchuck.domain.meetingLike.repository.MeetingLikeRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.burnchuck.common.enums.ErrorCode.ALREADY_LIKED_MEETING;

@Service
@RequiredArgsConstructor
public class MeetingLikeService {

    private final MeetingLikeRepository meetingLikeRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    /**
     *  좋아요 생성
     */
    @Transactional
    public MeetingLikeResponse createLike(AuthUser authUser, Long meetingId) {

        // 1. 로그인 유저 조회 (탈퇴 안 한 유저)
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 모임 조회 (삭제 여부 있으면 같이 체크)
        Meeting meeting = meetingRepository.findActivateUserById(meetingId);

        // 3. 중복 좋아요 방지
        if (meetingLikeRepository.existsByUserAndMeeting(user, meeting)) {
            throw new CustomException(ALREADY_LIKED_MEETING);
        }

        // 4. 좋아요 생성
        MeetingLike meetingLike = new MeetingLike(user, meeting);
        meetingLikeRepository.save(meetingLike);

        return MeetingLikeResponse.from(meeting);
    }

    /**
     *  좋아요 취소
     */
    @Transactional
    public void deleteLike(AuthUser authUser, Long meetingId) {

        // 1. 로그인 유저 조회 (탈퇴 안 한 유저)
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 모임 조회
        Meeting meeting = meetingRepository.findActivateUserById(meetingId);

        // 3. 좋아요 관계 조회
        MeetingLike meetingLike = meetingLikeRepository.findByUserAndMeetingOrThrow(user, meeting);

        // 4. 좋아요 삭제
        meetingLikeRepository.delete(meetingLike);
    }

    /**
     *  모임 별 좋아요 개수 조회
     */
    @Transactional(readOnly = true)
    public MeetingLikeCountResponse countLikes(Long meetingId) {

        // 1. 모임 조회 (삭제 안 된 모임)
        Meeting meeting = meetingRepository.findActivateUserById(meetingId);

        // 2. 좋아요 수 조회
        long likes = meetingLikeRepository.countByMeeting(meeting);

        // 3. 응답 반환
        return MeetingLikeCountResponse.of(likes);
    }
}
