package com.example.burnchuck.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    NEW_FOLLOWING_POST("\'{nickname}\'님이 새 모임 \'{title}\' 을/를 생성하였습니다."),
    MEETING_MEMBER_JOIN("\'{title}\' 모임에 \'{nickname}\'님이 참여하셨습니다."),
    MEETING_MEMBER_LEFT("\'{title}\' 모임에 \'{nickname}\'님이 탈퇴하셨습니다."),
    COMMENT_REQUESTED("\'{title}\' 모임, 어떠셨나요?\n만났던 사람들의 후기를 남겨주세요!");

    private final String description;
}
