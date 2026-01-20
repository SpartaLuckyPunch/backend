package com.example.burnchuck.domain.category.model.response;

import com.example.burnchuck.common.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private Long categoryId;
    private String code;
    private String category;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getCategory()
        );
    }
}
