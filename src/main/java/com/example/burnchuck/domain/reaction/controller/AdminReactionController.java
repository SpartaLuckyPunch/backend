package com.example.burnchuck.domain.reaction.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.reaction.dto.request.AdminReactionCreateRequest;
import com.example.burnchuck.domain.reaction.dto.response.AdminReactionCreateResponse;
import com.example.burnchuck.domain.reaction.service.AdminReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.REACTION_CREATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.REACTION_DELETE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminReactionController {

    private final AdminReactionService adminReactionService;

    /**
     * 리액션 종류 생성
     */
    @PostMapping("/reactions")
    public ResponseEntity<CommonResponse<AdminReactionCreateResponse>> createAdminReaction(
            @Valid @RequestBody AdminReactionCreateRequest request
    ) {
        AdminReactionCreateResponse response = adminReactionService.createAdminReaction(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(REACTION_CREATE_SUCCESS, response));
    }

    /**
     * 리액션 종류 삭제
     */
    @DeleteMapping("/reactions/{reactionId}")
    public ResponseEntity<CommonResponse<Void>> deleteAdminReaction(
            @PathVariable Long reactionId
    ) {
        adminReactionService.deleteAdminReaction(reactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(REACTION_DELETE_SUCCESS, null));
    }
}
