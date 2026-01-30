package com.example.burnchuck.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.burnchuck.common.enums.ValidationMessage.CHAT_CONTENT_NOT_NULL;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    @NotNull(message = CHAT_CONTENT_NOT_NULL)
    private String content;
}
