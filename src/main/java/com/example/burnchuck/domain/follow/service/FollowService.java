package com.example.burnchuck.domain.follow.service;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.follow.model.response.FollowCountResponse;
import com.example.burnchuck.domain.follow.model.response.FollowResponse;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.burnchuck.common.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    /**
     * 팔로우
     */
    @Transactional
    public FollowResponse follow(AuthUser user, Long userId) {

        // 팔로우 하는 사람(신청자) = follower
        // 팔로우 당하는 사람(대상) = followee
        // 1. follower 유저 조회
        User follower = userRepository.findActivateUserById(user.getId(), FOLLOWER_NOT_FOUND);

        // 2. followee 유저 조회
        User followee = userRepository.findActivateUserById(userId, FOLLOWEE_NOT_FOUND);

        // 3. 자기 자신 팔로우 예외처리
        if (follower.getId().equals(followee.getId())) {
            throw new CustomException(SELF_FOLLOW_NOT_ALLOWED);
        }

        // 4. 중복 팔로우 방지
        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new CustomException(ALREADY_FOLLOWING);
        }

        // 5. Follow 저장
        Follow follow = new Follow(follower, followee);
        followRepository.save(follow);

        return FollowResponse.from(follow);
    }


    /**
     * 언팔로우
     */
    @Transactional
    public void unfollow(AuthUser user, Long userId) {

        // 1. follower 조회
        User follower = userRepository.findActivateUserById(user.getId(), FOLLOWER_NOT_FOUND);

        // 2. followee 조회
        User followee = userRepository.findActivateUserById(userId, FOLLOWEE_NOT_FOUND);

        // 3. 자기 자신 언팔로우 방지
        if (follower.getId().equals(followee.getId())) {
            throw new CustomException(SELF_UNFOLLOW_NOT_ALLOWED);
        }

        // 4. 팔로우 관계 조회
        Follow follow = followRepository.getByFollowerAndFolloweeOrThrow(follower, followee, FOLLOW_NOT_FOUND);

        // 5. 언팔로우 (삭제)
        followRepository.delete(follow);
    }

    /**
     * 팔로잉 / 팔로워 수 조회
     */
    @Transactional(readOnly = true)
    public FollowCountResponse followCount(Long userId) {

        // 1. 유저 조회
        User targetUser = userRepository.findActivateUserById(userId, NOT_FOUND_USER);

        // 2. 팔로잉 수 (내가 팔로우한 사람 수)
        long followings = followRepository.countByFollower(targetUser);

        // 3. 팔로워 수 (나를 팔로우한 사람 수)
        long followers = followRepository.countByFollowee(targetUser);

        // 4. 응답 DTO 생성
        return FollowCountResponse.of(followings, followers);
    }
}
