package com.example.burnchuck.common.enums;

import lombok.Getter;

@Getter
public enum SuccessMessage {

    AUTH_LOGIN_SUCCESS("로그인 성공"),
    ;

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
