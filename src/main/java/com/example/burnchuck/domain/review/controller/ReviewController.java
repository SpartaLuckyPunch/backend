package com.example.burnchuck.domain.review.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.REVIEW_CREATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.REVIEW_GET_LIST_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.REVIEW_GET_ONE_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.domain.review.dto.request.ReviewCreateRequest;
import com.example.burnchuck.domain.review.dto.response.ReviewDetailResponse;
import com.example.burnchuck.domain.review.dto.response.ReviewGetListResponse;
import com.example.burnchuck.domain.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 후기 등록
     */
    @Operation(
            summary = "후기 등록",
            description = """
                    모임을 함께 했던 사람에 대한 후기를 생성합니다.
                    """
    )
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
    @Operation(
            summary = "후기 목록 조회",
            description = """
                    특정 사용자에 대한 후기 목록을 조회합니다.
                    """
    )
    @GetMapping("/reviews/users/{userId}")
    public ResponseEntity<CommonResponse<ReviewGetListResponse>> getReviewList(
            @PathVariable Long userId,
            @PageableDefault(sort = "createdDatetime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ReviewGetListResponse response = reviewService.getReviewList(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(REVIEW_GET_LIST_SUCCESS, response));
    }

    /**
     * 후기 단건조회
     */
    @Operation(
            summary = "후기 단건 조회",
            description = """
                    특정 사용자에 대한 특정 후기를 조회합니다.
                    """
    )
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewDetailResponse>> getReviewDetail(
            @PathVariable Long reviewId
    ) {
        ReviewDetailResponse response = reviewService.getReviewDetail(reviewId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(REVIEW_GET_ONE_SUCCESS, response));
    }
}
