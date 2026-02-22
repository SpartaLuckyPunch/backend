package com.example.burnchuck.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomUpdateEvent {
    private Long roomId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
