package com.example.burnchuck.domain.review.repository;

import com.example.burnchuck.common.entity.ReviewReaction;
import com.example.burnchuck.domain.review.model.response.ReactionCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {
    // 리뷰 ID로 모든 리액션 기록을 찾아오는 메서드
    List<ReviewReaction> findAllByReviewId(Long reviewId);

    // 특정 유저(reviewee)에게 달린 리뷰들의 리액션별 갯수를 구하는 쿼리
    @Query("SELECT new com.example.burnchuck.domain.review.model.response.ReactionCount(rr.reaction.reaction, COUNT(rr)) " +
            "FROM ReviewReaction rr " +
            "WHERE rr.review.reviewee.id = :revieweeId " +
            "GROUP BY rr.reaction.reaction")
    List<ReactionCount> countReactionsByRevieweeId(@Param("revieweeId") Long revieweeId);
}