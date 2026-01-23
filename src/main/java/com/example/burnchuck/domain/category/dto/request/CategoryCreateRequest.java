package com.example.burnchuck.domain.category.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryCreateRequest {

    private String code;
    private String category;
}
