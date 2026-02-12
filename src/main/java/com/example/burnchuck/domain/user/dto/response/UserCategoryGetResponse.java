package com.example.burnchuck.domain.user.dto.response;

import lombok.Getter;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCategoryGetResponse {

    private final List<String> categoryCodeList;
}
