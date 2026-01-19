package com.example.burnchuck.common.enums;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidationMessage {

    // --- User 관련 ---
    public static final String USERNAME_NOT_BLANK = "닉네임은 필수입니다.";
    public static final String USERNAME_SIZE = "닉네임은 영어 50자 / 한글 15자를 넘길 수 없습니다.";
    public static final String EMAIL_NOT_BLANK = "이메일은 필수입니다.";
    public static final String EMAIL_FORMAT = "올바른 이메일 형식이 아닙니다.";
    public static final String PASSWORD_NOT_BLANK = "비밀번호는 필수입니다.";
    public static final String BIRTHDATE_NOT_BLANK = "생년월일은 필수입니다.";
    public static final String PROVINCE_NOT_BLANK = "시/도는 필수입니다.";
    public static final String CITY_NOT_BLANK = "시/군/구는 필수입니다.";
    public static final String DISTRICT_NOT_BLANK = "읍/면/동은 필수입니다.";
    public static final String GENDER_NOT_BLANK = "성별은 필수입니다.";
    public static final String GENDER_PATTERN = "성별은 '남' 또는 '여'만 입력 가능합니다.";

    // --- Review 관련 ---
    public static final String RATING_MIN = "별점은 최소 1점 이상이어야 합니다.";
    public static final String RATING_MAX = "별점은 최대 5점까지 가능합니다.";
    public static final String RATING_NOT_NULL = "별점은 필수입니다.";
    public static final String ID_NOT_NULL = "ID 값은 필수입니다.";
    public static final String DETAILED_REVIEW_NOT_BLANK = "상세 리뷰 내용은 필수입니다";
}
