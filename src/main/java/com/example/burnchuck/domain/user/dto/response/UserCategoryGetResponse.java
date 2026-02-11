package com.example.burnchuck.domain.user.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class UserCategoryGetResponse {

    private final List<String> categoryCodeList;

    public UserCategoryGetResponse(List<String> categoryCodeList) {
        this.categoryCodeList = categoryCodeList;
    }
}
