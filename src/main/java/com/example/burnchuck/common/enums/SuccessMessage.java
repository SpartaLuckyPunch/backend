package com.example.burnchuck.common.enums;

import lombok.Getter;

@Getter
public enum SuccessMessage {

    // 인증
    AUTH_SIGNUP_SUCCESS("회원가입 성공"),
    AUTH_LOGIN_SUCCESS("로그인 성공"),

    // 유저
    USER_UPDATE_PROFILE_SUCCESS("프로필 수정 성공"),

    // 팔로우
    FOLLOW_SUCCESS("팔로우 성공"),
    UNFOLLOW_SUCCESS("언팔로우 성공"),

    // 카테고리
    CATEGORY_GET_SUCCESS("카테고리 조회 성공"),

    // 모임
    MEETING_CREATE_SUCCESS("모임 생성 성공")
    ;

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
