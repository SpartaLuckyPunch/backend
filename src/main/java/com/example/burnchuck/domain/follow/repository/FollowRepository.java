package com.example.burnchuck.domain.follow.repository;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowee(User follower, User followee);

    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);

    default Follow getByFollowerAndFolloweeOrThrow(
            User follower,
            User followee,
            ErrorCode errorCode
    ) {
        return findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new CustomException(errorCode));
    }
}
