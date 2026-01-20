package com.example.burnchuck.domain.review.model.response;

import com.example.burnchuck.common.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

// 외부 클래스
@Getter
@AllArgsConstructor
public class ReviewGetListResponse {

    private final List<ReactionCount> reactionCountList;
    private final Page<ReviewSummary> reviewList;

    public static ReviewGetListResponse of(List<ReactionCount> reactionCountList, Page<Review> reviews) {


        // 리뷰 요약 리스트
        Page<ReviewSummary> reviewPage = reviews.map(ReviewSummary::from);



        return new ReviewGetListResponse(reactionCountList, reviewPage);
    }

    // 내부 클래스
    @Getter
    @AllArgsConstructor
    public static class ReviewSummary {
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
}
