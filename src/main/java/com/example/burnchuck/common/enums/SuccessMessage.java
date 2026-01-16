package com.example.burnchuck.common.enums;

import lombok.Getter;

@Getter
public enum SuccessMessage {

    AUTH_SIGNUP_SUCCESS("회원가입 성공"),
    AUTH_LOGIN_SUCCESS("로그인 성공"),
    FOLLOW_SUCCESS("팔로우 성공"),
    UNFOLLOW_SUCCESS("언팔로우 성공")
    ;

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
