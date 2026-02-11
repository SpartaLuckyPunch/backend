package com.example.burnchuck.common.enums;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidationMessage {

    // --- User 관련 ---
    public static final String USERNAME_NOT_BLANK = "닉네임은 필수입니다.";
    public static final String USERNAME_SIZE = "닉네임은 영어 50자 / 한글 15자를 넘길 수 없습니다.";
    public static final String EMAIL_NOT_BLANK = "이메일은 필수입니다.";
    public static final String EMAIL_FORMAT = "올바른 이메일 형식이 아닙니다.";
    public static final String VERIFICATION_CODE_NOT_BLANK = "인증 번호는 필수입니다.";
    public static final String PASSWORD_NOT_BLANK = "비밀번호는 필수입니다.";
    public static final String BIRTHDATE_NOT_BLANK = "생년월일은 필수입니다.";
    public static final String PROVINCE_NOT_BLANK = "시/도는 필수입니다.";
    public static final String CITY_NOT_BLANK = "시/군/구는 필수입니다.";
    public static final String GENDER_NOT_BLANK = "성별은 필수입니다.";
    public static final String GENDER_PATTERN = "성별은 '남' 또는 '여'만 입력 가능합니다.";

    // --- Review 관련 ---
    public static final String RATING_MIN = "별점은 최소 1점 이상이어야 합니다.";
    public static final String RATING_MAX = "별점은 최대 5점까지 가능합니다.";
    public static final String RATING_NOT_NULL = "별점은 필수입니다.";
    public static final String ID_NOT_NULL = "ID 값은 필수입니다.";

    // --- Meeting 관련 ---
    public static final String MEETING_TITLE_NOT_BLANK = "모임 제목은 필수입니다.";
    public static final String MEETING_TITLE_SIZE = "모임 제목은 50자 이내여야 합니다.";
    public static final String MEETING_DESCRIPTION_NOT_BLANK = "모임 설명은 필수입니다.";
    public static final String MEETING_DESCRIPTION_SIZE = "모임 설명은 500자 이내여야 합니다.";
    public static final String MEETING_IMG_URL_NOT_BLANK = "이미지 URL은 필수입니다.";
    public static final String MEETING_IMG_URL_FORMAT = "이미지 URL 형식이 올바르지 않습니다.";
    public static final String MEETING_LOCATION_NOT_BLANK = "모임 장소는 필수입니다.";
    public static final String MEETING_LATITUDE_NOT_NULL = "위도는 필수입니다.";
    public static final String MEETING_LATITUDE_RANGE = "위도는 -90 이상 90 이하여야 합니다.";
    public static final String MEETING_LONGITUDE_NOT_NULL = "경도는 필수입니다.";
    public static final String MEETING_LONGITUDE_RANGE = "경도는 -180 이상 180 이하여야 합니다.";
    public static final String MEETING_DATETIME_NOT_NULL = "모임 날짜와 시간은 필수입니다.";
    public static final String MEETING_DATETIME_FUTURE = "모임 시간은 현재 이후여야 합니다.";
    public static final String MEETING_CATEGORY_NOT_NULL = "카테고리는 필수입니다.";
    public static final String MEETING_MAX_ATTENDEES_NOT_NULL = "참여자 수는 필수 값입니다.";

    // --- Chat 관련 ---
    public static final String CHAT_CONTENT_NOT_NULL = "채팅 내용은 필수입니다.";
    public static final String CHAT_TARGET_USER_NOT_NULL = "타겟 유저는 필수입니다.";
    public static final String CHAT_ROOM_NAME_NOT_NULL = "채팅 제목은 필수입니다.";
    public static final String CHAT_ROOM_NAME_SIZE = "채팅 제목 길이는 최대 50자 입니다.";

}
