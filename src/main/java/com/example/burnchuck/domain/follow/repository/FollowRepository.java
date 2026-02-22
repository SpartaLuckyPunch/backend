package com.example.burnchuck.domain.follow.repository;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowee(User follower, User followee);

    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);

    void deleteByFollowerId(Long id);

    void deleteByFolloweeId(Long id);

    long countByFollower(User follower);

    long countByFollowee(User followee);

    // 팔로잉 목록 조회
    @Query("""
        SELECT f
        FROM Follow f
        JOIN FETCH f.followee
        WHERE f.follower = :follower
        """)
    List<Follow> findAllByFollower(@Param("follower") User follower);

    // 팔로워 목록 조회
    @Query("""
        SELECT f
        FROM Follow f
        JOIN FETCH f.follower
        WHERE f.followee = :followee
        """)
    List<Follow> findAllByFollowee(@Param("followee") User followee);

    default Follow getByFollowerAndFolloweeOrThrow(User follower, User followee) {
        return findByFollowerAndFollowee(follower, followee)
            .orElseThrow(() -> new CustomException(ErrorCode.FOLLOW_NOT_FOUND));
    }
}
