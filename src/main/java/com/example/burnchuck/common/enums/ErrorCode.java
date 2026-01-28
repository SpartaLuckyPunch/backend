package com.example.burnchuck.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 인증
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    EMAIL_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    NICKNAME_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_GENDER_FORMAT(HttpStatus.BAD_REQUEST, "성별은 '남' 또는 '여'만 입력 가능합니다."),

    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소가 존재하지 않습니다."),

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호와 새 비밀번호가 동일합니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 권한입니다."),

    // 팔로우
    FOLLOWER_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로워가 존재하지 않습니다."),
    FOLLOWEE_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로이가 존재하지 않습니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 관계가 존재하지 않습니다."),
    SELF_UNFOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 언팔로우할 수 없습니다."),
    SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우할 수 없습니다."),
    ALREADY_FOLLOWING(HttpStatus.CONFLICT, "이미 팔로우한 유저입니다."),

    // 리뷰
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."),
    REVIEWER_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰 작성자가 존재하지 않습니다."),
    REVIEWEE_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰 대상자가 존재하지 않습니다."),
    SELF_REVIEW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신에게는 리뷰를 남길 수 없습니다."),
    ALREADY_REVIEWED(HttpStatus.CONFLICT, "이미 해당 모임에서 해당 유저에게 리뷰를 남겼습니다."),
    REACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리액션 종류입니다."),
      
    // 좋아요
    ALREADY_LIKED_MEETING(HttpStatus.CONFLICT, "이미 좋아요한 번개입니다."),
    MEETING_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요하지 않은 번개입니다."),

    // 모임
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 번개입니다."),
    HOST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주최자입니다"),

    // 모임 참여
    ATTENDANCE_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 참여 신청한 번개입니다."),
    ATTENDANCE_CANNOT_REGISTER(HttpStatus.BAD_REQUEST, "모집 완료된 번개입니다."),
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "번개 참여 신청이 존재하지 않습니다."),
    ATTENDANCE_HOST_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "호스트는 번개 참여를 취소할 수 없습니다."),
    ATTENDANCE_CANNOT_CANCEL_WHEN_MEETING_CLOSED(HttpStatus.BAD_REQUEST, "번개 시작 10분 전에는 취소할 수 없습니다."),

    // 카테고리
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리가 존재하지 않습니다."),

    // 알림
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림이 존재하지 않습니다."),

    // 락
    LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "락 획득을 실패했습니다"),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
