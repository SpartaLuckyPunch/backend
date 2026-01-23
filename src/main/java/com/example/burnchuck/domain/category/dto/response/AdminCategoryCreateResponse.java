package com.example.burnchuck.domain.category.dto.response;

import com.example.burnchuck.common.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminCategoryCreateResponse {

    private Long id;
    private String code;
    private String category;

    public static AdminCategoryCreateResponse from(Category category) {
        return new AdminCategoryCreateResponse(
                category.getId(),
                category.getCode(),
                category.getCategory()
        );
    }
}
