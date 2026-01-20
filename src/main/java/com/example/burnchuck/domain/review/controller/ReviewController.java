package com.example.burnchuck.domain.review.controller;


import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.review.model.request.ReviewCreateRequest;
import com.example.burnchuck.domain.review.model.response.ReviewGetListResponse;
import com.example.burnchuck.domain.review.model.response.ReviewSummary;
import com.example.burnchuck.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.REVIEW_CREATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.REVIEW_GET_SUCCESS;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 후기 등록
     */
    @PostMapping("/users/{revieweeId}/review")
    public ResponseEntity<CommonResponse<Void>> createReview(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long revieweeId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        reviewService.createReview(authUser, revieweeId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.successNodata(REVIEW_CREATE_SUCCESS));

    }

    /**
     * 후기 목록조회
     */
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<CommonResponse<ReviewGetListResponse<PageResponse<ReviewSummary>>>> getReviewList(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdDatetime", direction = Sort.Direction.DESC) Pageable pageable
    ){
        ReviewGetListResponse<PageResponse<ReviewSummary>> response = reviewService.getReviewList(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(REVIEW_GET_SUCCESS,response));
    }

}
