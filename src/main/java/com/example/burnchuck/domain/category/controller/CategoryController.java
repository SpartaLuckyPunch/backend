package com.example.burnchuck.domain.category.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.category.dto.request.CategoryCreateRequest;
import com.example.burnchuck.domain.category.dto.response.CategoryListResponse;
import com.example.burnchuck.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 목록 조회
     */
    @GetMapping("/categories")
    public ResponseEntity<CommonResponse<CategoryListResponse>> getCategories() {
        CategoryListResponse response = categoryService.getCategoryList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CATEGORY_GET_SUCCESS, response));
    }

    /**
     * 카테고리 생성(관리자 전용)
     */
    @PostMapping("/admin/categories")
    public ResponseEntity<CommonResponse<Void>> createAdminCategory(
            @Valid @RequestBody CategoryCreateRequest request
    ) {
        categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.successNodata(CATEGORY_CREATE_SUCCESS));

    }

    /**
     * 카테고리 삭제(관리자 전용)
     */
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CommonResponse<Void>> deleteAdminCategory(
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(CATEGORY_DELETE_SUCCESS));
    }

}
