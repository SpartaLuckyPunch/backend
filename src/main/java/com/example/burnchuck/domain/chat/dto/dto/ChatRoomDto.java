package com.example.burnchuck.domain.chat.dto.dto;

import com.example.burnchuck.common.entity.ChatMessage;
import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomDto {
    private final Long roomId;
    private final String name;
    private final RoomType roomType;
    private final String chatRoomImg;
    private final String lastMessage;
    private final LocalDateTime lastMessageTime;
    private final int memberCount;
    // private final int unreadCount;  // (안 읽은 개수도 필요하지 않을까?)

    public static ChatRoomDto of(ChatRoom room, String name, ChatMessage lastMsg, String chatRoomImg, int memberCount) {
        return new ChatRoomDto(
                room.getId(),
                name,
                room.getType(),
                chatRoomImg,
                lastMsg != null ? lastMsg.getContent() : null,
                lastMsg != null ? lastMsg.getCreatedDatetime() : room.getCreatedDatetime(),
                memberCount
        );
    }


}
