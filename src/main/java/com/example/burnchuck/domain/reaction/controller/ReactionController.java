package com.example.burnchuck.domain.reaction.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.REVIEW_REACTION_GET_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.reaction.service.ReactionService;
import com.example.burnchuck.domain.review.dto.response.ReactionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    /**
     * 전체 후기 리액션 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<List<ReactionResponse>>> getReviewReactionList() {

        List<ReactionResponse> response = reactionService.getReviewReactionList();

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(REVIEW_REACTION_GET_SUCCESS, response));
    }
}
