package com.example.burnchuck.domain.category.model.response;

import com.example.burnchuck.common.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryListResponse {

    private List<CategoryResponse> categories;

    public static CategoryListResponse from(List<Category> categories) {
        return new CategoryListResponse(
                categories.stream()
                        .map(CategoryResponse::from)
                        .toList()
        );
    }
}
