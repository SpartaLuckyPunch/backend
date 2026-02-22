package com.example.burnchuck.domain.chat.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatReadEvent {
    private Long roomId;
    private Long userId;      // 누가 읽었는지
    private Long sequence;    // 어디까지 읽었는지
}
