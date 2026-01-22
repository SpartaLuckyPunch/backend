package com.example.burnchuck.domain.category.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.category.model.response.CategoryListResponse;
import com.example.burnchuck.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.burnchuck.common.enums.SuccessMessage.CATEGORY_GET_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<CategoryListResponse>> getCategories() {
        CategoryListResponse response = categoryService.getCategoryList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CATEGORY_GET_SUCCESS, response));
    }

}
