package com.example.burnchuck.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    NEW_FOLLOWING_POST("'%s'님이 새 모임 '%s'을/를 생성하였습니다."),
    MEETING_MEMBER_JOIN("'%s' 모임에 '%s'님이 참여하셨습니다."),
    MEETING_MEMBER_LEFT("'%s' 모임에 '%s'님이 탈퇴하셨습니다."),
    COMMENT_REQUESTED("'%s' 모임, 어떠셨나요?\n만났던 사람들의 후기를 남겨주세요!");

    private final String description;

    public String getDescription(NotificationType type, String title, String nickname) {

        return switch (type) {
            case NEW_FOLLOWING_POST -> String.format(description, nickname, title);
            case MEETING_MEMBER_JOIN, MEETING_MEMBER_LEFT -> String.format(description, title, nickname);
            case COMMENT_REQUESTED -> String.format(description, title);
        };
    }
}
