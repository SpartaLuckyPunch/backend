package com.example.burnchuck.domain.chat.dto.request;

import static com.example.burnchuck.common.enums.ValidationMessage.CHAT_TARGET_USER_NOT_NULL;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomCreateRequest {
    @NotNull(message = CHAT_TARGET_USER_NOT_NULL)
    private Long targetUserId;
}
