package com.example.burnchuck.domain.review.model.response;

import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.common.entity.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewGetListResponse<T> {

    private List<ReactionCount> reactionCountList;
    private T reviewList; // PageResponse가 담길 자리

    public static ReviewGetListResponse<PageResponse<ReviewSummary>> of(List<ReactionCount> reactionCounts, Page<Review> reviews) {

        // 1. 외부로 뺀 ReviewSummary의 from 메서드 사용
        Page<ReviewSummary> reviewSummaryPage = reviews.map(ReviewSummary::from);

        // 2. 공통 규격인 PageResponse로 변환
        PageResponse<ReviewSummary> pageResponse = PageResponse.from(reviewSummaryPage);

        return new ReviewGetListResponse<>(reactionCounts, pageResponse);
    }
}