package com.example.burnchuck.domain.chat.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomCreationResult {
    private final Long roomId;
    private final boolean isCreated; // true: 신규 생성, false: 이미 존재
}
