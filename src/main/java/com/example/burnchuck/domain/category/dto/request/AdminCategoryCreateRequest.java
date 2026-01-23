package com.example.burnchuck.domain.category.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminCategoryCreateRequest {

    private String code;
    private String category;
}
