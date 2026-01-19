package com.example.burnchuck.domain.review.model.response;

import com.example.burnchuck.common.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 외부 클래스
@Getter
@AllArgsConstructor
public class ReviewGetListResponse {

    private final List<ReactionCount> reactionCountList;
    private final List<ReviewSummary> reviewList;

    public static ReviewGetListResponse of(Map<String, Long> reactionCountMap, List<Review> reviews) {

        // 반응 갯수 리스트
        List<ReactionCount> reactionCountList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : reactionCountMap.entrySet()) {
            reactionCountList.add(ReactionCount.of(entry.getKey(), entry.getValue()));
        }

        // 리뷰 요약 리스트
        List<ReviewSummary> reviewList = new ArrayList<>();
        for (Review review : reviews) {
            reviewList.add(ReviewSummary.from(review));
        }

        return new ReviewGetListResponse(reactionCountList, reviewList);
    }

    // 내부 클래스
    @Getter
    @AllArgsConstructor
    public static class ReactionCount {
        private final String reaction;
        private final Long count;

        public static ReactionCount of(String reaction, Long count) {
            return new ReactionCount(reaction, count);
        }
    }

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
