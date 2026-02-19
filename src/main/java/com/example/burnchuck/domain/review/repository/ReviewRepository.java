package com.example.burnchuck.domain.review.repository;

import com.example.burnchuck.common.entity.Review;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByMeetingIdAndReviewerIdAndRevieweeId(Long meetingId, Long reviewerId, Long revieweeId);

    @Query("""
          SELECT AVG(r.rating)
          FROM Review r
          WHERE r.reviewee = :reviewee
          """)
    Double findAvgRatesByReviewee(@Param("reviewee") User reviewee);

    Page<Review> findAllByRevieweeId(Long revieweeId, Pageable pageable);

    default Review findReviewById(Long reviewId) {
        return findById(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
