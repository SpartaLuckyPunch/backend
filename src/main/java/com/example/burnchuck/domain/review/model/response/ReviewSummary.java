package com.example.burnchuck.domain.review.model.response;

import com.example.burnchuck.common.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewSummary{
    private final Long reviewId;
    private final Long reviewerId;
    private final String reviewerProfileImgUrl;
    private final String reviewerNickname;
    private final Integer rating;
    private final String detailedReview;

    public static ReviewSummary from(Review review) {
        return new ReviewSummary(
                review.getId(),
                review.getReviewer().getId(),
                review.getReviewer().getProfileImgUrl(),
                review.getReviewer().getNickname(),
                review.getRating(),
                review.getDetailedReview()
        );
    }
}