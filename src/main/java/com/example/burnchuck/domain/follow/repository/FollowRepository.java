package com.example.burnchuck.domain.follow.repository;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowee(User follower, User followee);
}
