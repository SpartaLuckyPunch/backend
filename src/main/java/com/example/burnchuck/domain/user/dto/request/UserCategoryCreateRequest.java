package com.example.burnchuck.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserCategoryCreateRequest {

    private List<String> categoryCodeList;
}
