package com.example.burnchuck.domain.category.dto.response;

import com.example.burnchuck.common.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryCreateResponse {

    private Long id;
    private String code;
    private String category;

    public static CategoryCreateResponse from(Category category) {
        return new CategoryCreateResponse(
                category.getId(),
                category.getCode(),
                category.getCategory()
        );
    }
}
