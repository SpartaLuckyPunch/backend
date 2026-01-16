package com.example.burnchuck.domain.follow.service;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.exception.CustomException;
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
     * 팔로우 로직
     */
    @Transactional
    public FollowResponse follow(Long userId) {

        // 팔로우 하는 사람(신청자) = follower
        // 팔로우 당하는 사람(대상) = followee
        // 1. follower 유저 조회 (현재 더미를 넣어 둔 상태이다.)
        User follower = userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(FOLLOWER_NOT_FOUND));

        // 2. followee 유저 조회
        User followee = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(FOLLOWEE_NOT_FOUND));

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

}
