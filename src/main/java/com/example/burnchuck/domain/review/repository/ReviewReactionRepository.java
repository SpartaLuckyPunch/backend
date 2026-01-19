package com.example.burnchuck.domain.review.repository;

import com.example.burnchuck.common.entity.ReviewReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {

    // 특정 유저(reviewee)에게 달린 리뷰들의 리액션별 갯수를 구하는 쿼리
    @Query("SELECT rr.reaction.reaction, COUNT(rr) " +
            "FROM ReviewReaction rr " +
            "WHERE rr.review.reviewee.id = :revieweeId " +
            "GROUP BY rr.reaction.reaction")
    List<Object[]> countReactionsByRevieweeId(@Param("revieweeId") Long revieweeId);
}