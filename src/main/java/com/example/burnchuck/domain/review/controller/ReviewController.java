package com.example.burnchuck.domain.review.controller;


import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.review.model.request.ReviewCreateRequest;
import com.example.burnchuck.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.REVIEW_CREATE_SUCCESS;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{revieweeId}/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> crateReviewApi(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long revieweeId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        reviewService.createReview(authUser.getId(), revieweeId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.successNodata(REVIEW_CREATE_SUCCESS));

    }
}
