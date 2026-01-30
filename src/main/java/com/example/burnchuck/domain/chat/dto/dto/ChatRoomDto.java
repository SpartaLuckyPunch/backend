package com.example.burnchuck.domain.chat.dto.dto;

import com.example.burnchuck.common.entity.ChatMessage;
import com.example.burnchuck.common.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomDto {
    private final Long roomId;
    private final String name;
    private final String lastMessage;
    private final LocalDateTime lastMessageTime;
    private final int memberCount;
    // private final int unreadCount;  // (안 읽은 개수도 필요하지 않을까?)

    public static ChatRoomDto of(ChatRoom room, String name, ChatMessage lastMsg, int memberCount) {
        return new ChatRoomDto(
                room.getId(),
                name,
                lastMsg != null ? lastMsg.getContent() : null,
                lastMsg != null ? lastMsg.getCreatedDatetime() : room.getCreatedDatetime(),
                memberCount
        );
    }


}
