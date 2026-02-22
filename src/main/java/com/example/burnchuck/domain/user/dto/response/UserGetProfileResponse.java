package com.example.burnchuck.domain.user.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserGetProfileResponse {

    private final String profileImgUrl;
    private final String nickname;
    private final Long followings;
    private final Long followers;
    private final Double avgRates;
}
