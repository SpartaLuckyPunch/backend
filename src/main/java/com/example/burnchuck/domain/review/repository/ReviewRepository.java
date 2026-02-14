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

    // 세 가지 조건으로 기존 리뷰 존재 여부 확인
    boolean existsByMeetingIdAndReviewerIdAndRevieweeId(Long meetingId, Long reviewerId, Long revieweeId);

    @Query("""
          SELECT AVG(r.rating)
          FROM Review r
          WHERE r.reviewee = :reviewee
          """)
    Double findAvgRatesByReviewee(@Param("reviewee") User reviewee);

    // reviewee에 대한 리뷰 목록를 페이징하여 최신순 조회(내림차순)
    Page<Review> findAllByRevieweeId(Long revieweeId, Pageable pageable);

    // 디폴트 메서드 추가
    default Review findReviewById(Long reviewId) {
        return findById(reviewId)
            .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
