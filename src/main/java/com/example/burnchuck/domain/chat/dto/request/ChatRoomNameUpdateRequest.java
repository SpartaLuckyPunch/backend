package com.example.burnchuck.domain.chat.dto.request;

import static com.example.burnchuck.common.constants.ValidationMessage.CHAT_ROOM_NAME_NOT_NULL;
import static com.example.burnchuck.common.constants.ValidationMessage.CHAT_ROOM_NAME_SIZE;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomNameUpdateRequest {
    @NotNull(message = CHAT_ROOM_NAME_NOT_NULL)
    @Size(max = 50, message = CHAT_ROOM_NAME_SIZE)
    private String name;
}