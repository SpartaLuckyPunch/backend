package com.example.burnchuck.domain.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "카테고리 코드는 필수입니다.")
    private String code;

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String category;
}
