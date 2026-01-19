package com.example.burnchuck.common.enums;

import lombok.Getter;

@Getter
public enum SuccessMessage {

    // 인증
    AUTH_SIGNUP_SUCCESS("회원가입 성공"),
    AUTH_LOGIN_SUCCESS("로그인 성공"),

    // 유저
    USER_UPDATE_PROFILE_SUCCESS("프로필 수정 성공"),
    USER_UPDATE_PASSWORD_SUCCESS("비밀번호 변경 성공"),
    USER_DELETE_SUCCESS("회원 탈퇴 성공"),
    USER_GET_PROFILE_SUCCESS("회원 프로필 조회 성공"),

    // 팔로우
    FOLLOW_SUCCESS("팔로우 성공"),
    UNFOLLOW_SUCCESS("언팔로우 성공"),
    GET_SUCCESS("조회 성공"),
    GET_FOLLOWING_SUCCESS("팔로잉 목록 조회 성공"),
    GET_FOLLOWER_SUCCESS("팔로워 목록 조회 성공"),

    // 리뷰
    REVIEW_CREATE_SUCCESS("후기 등록 성공")
    ;

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
