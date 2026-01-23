package com.example.burnchuck.domain.reaction.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.reaction.dto.request.ReactionCreateRequest;
import com.example.burnchuck.domain.reaction.dto.response.ReactionCreateResponse;
import com.example.burnchuck.domain.reaction.service.ReactionService;
import com.example.burnchuck.domain.review.dto.response.ReactionResponse;

import java.util.List;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReactionController {

    private final ReactionService reactionService;

    /**
     * 전체 후기 리액션 조회
     */
    @GetMapping("/reactions")
    public ResponseEntity<CommonResponse<List<ReactionResponse>>> getReviewReactionList() {

        List<ReactionResponse> response = reactionService.getReviewReactionList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(REVIEW_REACTION_GET_SUCCESS, response));
    }

    /**
     * 리액션 종류 생성(관리자 전용)
     */
    @PostMapping("/admin/reactions")
    public ResponseEntity<CommonResponse<ReactionCreateResponse>> createAdminReaction(
            @Valid @RequestBody ReactionCreateRequest request
    ) {
        ReactionCreateResponse response = reactionService.createReaction(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(REACTION_CREATE_SUCCESS, response));
    }

    /**
     * 리액션 종류 삭제(관리자 전용)
     */
    @DeleteMapping("/admin/reactions/{reactionId}")
    public ResponseEntity<CommonResponse<Void>> deleteAdminReaction(
            @PathVariable Long reactionId
    ) {
        reactionService.deleteReaction(reactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(REACTION_DELETE_SUCCESS));
    }
}
