package com.example.burnchuck.domain.follow.service;

import static com.example.burnchuck.common.enums.ErrorCode.ALREADY_FOLLOWING;
import static com.example.burnchuck.common.enums.ErrorCode.FOLLOWEE_NOT_FOUND;
import static com.example.burnchuck.common.enums.ErrorCode.FOLLOWER_NOT_FOUND;
import static com.example.burnchuck.common.enums.ErrorCode.SELF_FOLLOW_NOT_ALLOWED;
import static com.example.burnchuck.common.enums.ErrorCode.SELF_UNFOLLOW_NOT_ALLOWED;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.follow.dto.response.FollowCountResponse;
import com.example.burnchuck.domain.follow.dto.response.FollowListResponse;
import com.example.burnchuck.domain.follow.dto.response.FollowResponse;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    /**
     * 팔로우
     * 팔로우 하는 사람(신청자) = follower
     * 팔로우 당하는 사람(대상) = followee
     */
    @Transactional
    public FollowResponse follow(AuthUser user, Long userId) {

        User follower = userRepository.findActivateUserById(user.getId(), FOLLOWER_NOT_FOUND);

        User followee = userRepository.findActivateUserById(userId, FOLLOWEE_NOT_FOUND);

        if (ObjectUtils.nullSafeEquals(follower.getId(), followee.getId())) {
            throw new CustomException(SELF_FOLLOW_NOT_ALLOWED);
        }

        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new CustomException(ALREADY_FOLLOWING);
        }

        Follow follow = new Follow(follower, followee);
        followRepository.save(follow);

        return FollowResponse.from(follow);
    }


    /**
     * 언팔로우
     */
    @Transactional
    public void unfollow(AuthUser user, Long userId) {

        User follower = userRepository.findActivateUserById(user.getId(), FOLLOWER_NOT_FOUND);

        User followee = userRepository.findActivateUserById(userId, FOLLOWEE_NOT_FOUND);

        if (ObjectUtils.nullSafeEquals(follower.getId(), followee.getId())) {
            throw new CustomException(SELF_UNFOLLOW_NOT_ALLOWED);
        }

        Follow follow = followRepository.getByFollowerAndFolloweeOrThrow(follower, followee);

        followRepository.delete(follow);
    }

    /**
     * 팔로잉 / 팔로워 수 조회
     */
    @Transactional(readOnly = true)
    public FollowCountResponse followCount(Long userId) {

        User targetUser = userRepository.findActivateUserById(userId);

        long followings = followRepository.countByFollower(targetUser);
        long followers = followRepository.countByFollowee(targetUser);

        return FollowCountResponse.of(followings, followers);
    }

    /**
     * 팔로잉 목록 조회
     */
    @Transactional(readOnly = true)
    public FollowListResponse followingList(Long userId) {

        User user = userRepository.findActivateUserById(userId);

        List<Follow> follows = followRepository.findAllByFollower(user);

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

        User user = userRepository.findActivateUserById(userId);

        List<Follow> follows = followRepository.findAllByFollowee(user);

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

    /**
     * 팔로우 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean checkFollowExistence(Long userId, AuthUser authUser) {

        User follower = userRepository.findActivateUserById(authUser.getId(), FOLLOWER_NOT_FOUND);
        User followee = userRepository.findActivateUserById(userId, FOLLOWEE_NOT_FOUND);

        return followRepository.existsByFollowerAndFollowee(follower, followee);
    }
}
