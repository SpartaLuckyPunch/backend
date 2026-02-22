package com.example.burnchuck.domain.review.dto.response;

import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.common.entity.Review;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class ReviewGetListResponse {

    private List<ReactionCount> reactionCountList;
    private PageResponse<ReviewSummary> reviewList;

    public static ReviewGetListResponse of(List<ReactionCount> reactionCounts, Page<Review> reviews) {

        Page<ReviewSummary> reviewSummaryPage = reviews.map(ReviewSummary::from);

        PageResponse<ReviewSummary> pageResponse = PageResponse.from(reviewSummaryPage);

        return new ReviewGetListResponse(reactionCounts, pageResponse);
    }
}