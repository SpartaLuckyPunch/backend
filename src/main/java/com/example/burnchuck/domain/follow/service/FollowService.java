package com.example.burnchuck.domain.follow.service;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.follow.model.response.FollowCountResponse;
import com.example.burnchuck.domain.follow.model.response.FollowListResponse;
import com.example.burnchuck.domain.follow.model.response.FollowResponse;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        User targetUser = userRepository.findActivateUserById(userId);

        // 2. 팔로잉 수 (내가 팔로우한 사람 수)
        long followings = followRepository.countByFollower(targetUser);

        // 3. 팔로워 수 (나를 팔로우한 사람 수)
        long followers = followRepository.countByFollowee(targetUser);

        // 4. 응답 DTO 생성
        return FollowCountResponse.of(followings, followers);
    }

    /**
     * 팔로잉 목록 조회
     */
    @Transactional(readOnly = true)
    public FollowListResponse followingList(Long userId) {

        // 1. 기준 유저 조회 (탈퇴 안 한 유저)
        User user = userRepository.findActivateUserById(userId);

        // 2. 팔로잉 관계 조회
        List<Follow> follows = followRepository.findAllByFollower(user);

        // 3. followee → 응답 DTO 변환
        List<FollowListResponse.FollowUserDto> users =
                follows.stream()
                        .map(follow -> {
                            User followee = follow.getFollowee();
                            return new FollowListResponse.FollowUserDto(
                                    followee.getId(),
                                    followee.getNickname()
                            );
                        })
                        .toList();

        return new FollowListResponse(users);
    }

    /**
     * 팔로워 목록 조회
     */
    @Transactional(readOnly = true)
    public FollowListResponse followerList(Long userId) {

        // 1. 기준 유저 조회
        User user = userRepository.findActivateUserById(userId);

        // 2. 팔로워 관계 조회
        List<Follow> follows = followRepository.findAllByFollowee(user);

        // 3. follower → 응답 DTO 변환
        List<FollowListResponse.FollowUserDto> users =
                follows.stream()
                        .map(follow -> {
                            User follower = follow.getFollower();
                            return new FollowListResponse.FollowUserDto(
                                    follower.getId(),
                                    follower.getNickname()
                            );
                        })
                        .toList();

        return new FollowListResponse(users);
    }
}
