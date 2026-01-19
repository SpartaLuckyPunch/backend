package com.example.burnchuck.domain.review.repository;

import com.example.burnchuck.common.entity.Review;
import com.example.burnchuck.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 세 가지 조건으로 기존 리뷰 존재 여부 확인
    boolean existsByMeetingIdAndReviewerIdAndRevieweeId(Long meetingId, Long reviewerId, Long revieweeId);

    List<Review> findAllByReviewee(User reviewee);
}
