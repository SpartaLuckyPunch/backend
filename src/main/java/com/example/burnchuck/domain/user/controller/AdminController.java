package com.example.burnchuck.domain.user.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.category.dto.request.AdminCategoryCreateRequest;
import com.example.burnchuck.domain.reaction.dto.request.AdminReactionCreateRequest;
import com.example.burnchuck.domain.category.dto.response.AdminCategoryCreateResponse;
import com.example.burnchuck.domain.reaction.dto.response.AdminReactionCreateResponse;
import com.example.burnchuck.domain.user.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * 카테고리 생성
     */
    @PostMapping("/categories")
    public ResponseEntity<CommonResponse<AdminCategoryCreateResponse>> createAdminCategory(
            @Valid @RequestBody AdminCategoryCreateRequest request
    ) {
        AdminCategoryCreateResponse response = adminService.createAdminCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(CATEGORY_CREATE_SUCCESS, response));

    }

    /**
     * 카테고리 삭제
     */
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<CommonResponse<Void>> deleteAdminCategory(
            @PathVariable Long categoryId
    ) {
        adminService.deleteAdminCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CATEGORY_DELETE_SUCCESS, null));
    }

    /**
     * 리액션 종류 생성
     */
    @PostMapping("/reactions")
    public ResponseEntity<CommonResponse<AdminReactionCreateResponse>> createAdminReaction(
            @Valid @RequestBody AdminReactionCreateRequest request
    ) {
        AdminReactionCreateResponse response = adminService.createAdminReaction(request);

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
        adminService.deleteAdminReaction(reactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(REACTION_DELETE_SUCCESS, null));
    }
}
