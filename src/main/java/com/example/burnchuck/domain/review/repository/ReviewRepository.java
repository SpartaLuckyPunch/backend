package com.example.burnchuck.domain.review.repository;

import com.example.burnchuck.common.entity.Review;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 디폴트 메서드 추가
    default Review findReviewByById(Long reviewId) {
        return findById(reviewId).orElseThrow(() ->
                new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }

    // 세 가지 조건으로 기존 리뷰 존재 여부 확인
    boolean existsByMeetingIdAndReviewerIdAndRevieweeId(Long meetingId, Long reviewerId, Long revieweeId);

    List<Review> findAllByRevieweeId(Long revieweeId);

    // reviewee에 대한 리뷰 목록를 페이징하여 최신순 조회(내림차순)
    Page<Review> findAllByRevieweeId(Long revieweeId, Pageable pageable);
}
