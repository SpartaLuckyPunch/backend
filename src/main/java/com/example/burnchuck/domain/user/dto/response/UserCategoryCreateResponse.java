package com.example.burnchuck.domain.user.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class UserCategoryCreateResponse {

    public final List<String> userCategoryList;

    public UserCategoryCreateResponse(List<String> userCategoryList) {
        this.userCategoryList = userCategoryList;
    }
}
