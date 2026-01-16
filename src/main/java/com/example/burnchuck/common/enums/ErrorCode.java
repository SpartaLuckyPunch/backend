package com.example.burnchuck.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 인증
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    EMAIL_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    NICKNAME_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_GENDER_FORMAT(HttpStatus.BAD_REQUEST, "성별은 '남' 또는 '여'만 입력 가능합니다."),
    NOT_FOUND_ADDRESS(HttpStatus.NOT_FOUND, "존재하지 않는 주소입니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
