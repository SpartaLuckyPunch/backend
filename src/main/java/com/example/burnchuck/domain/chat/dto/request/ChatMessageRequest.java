package com.example.burnchuck.domain.chat.dto.request;

import static com.example.burnchuck.common.constants.ValidationMessage.CHAT_CONTENT_NOT_NULL;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ChatMessageRequest {
    @NotNull(message = CHAT_CONTENT_NOT_NULL)
    private String content;
}
