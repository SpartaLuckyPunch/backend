package com.example.burnchuck.domain.category.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.category.dto.request.AdminCategoryCreateRequest;
import com.example.burnchuck.domain.category.dto.response.AdminCategoryCreateResponse;
import com.example.burnchuck.domain.category.service.AdminCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.CATEGORY_CREATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CATEGORY_DELETE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    /**
     * 카테고리 생성
     */
    @PostMapping("/categories")
    public ResponseEntity<CommonResponse<AdminCategoryCreateResponse>> createAdminCategory(
            @Valid @RequestBody AdminCategoryCreateRequest request
    ) {
        AdminCategoryCreateResponse response = adminCategoryService.createAdminCategory(request);

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
        adminCategoryService.deleteAdminCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CATEGORY_DELETE_SUCCESS, null));
    }


}
