package com.example.burnchuck.domain.review.dto.response;

import com.example.burnchuck.common.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewDetailResponse {

    private final Long reviewId;
    private final Long reviewerId;
    private final String reviewerProfileImgUrl;
    private final String reviewerNickname;
    private final Integer rating;
    private final List<ReactionResponse> reactionList;
    private final String detailedReview;

    public static ReviewDetailResponse of(Review review, List<ReactionResponse> reactions) {
        return new ReviewDetailResponse(
                review.getId(),
                review.getReviewer().getId(),
                review.getReviewer().getProfileImgUrl(),
                review.getReviewer().getNickname(),
                review.getRating(),
                reactions,
                review.getDetailedReview()
        );
    }
}
