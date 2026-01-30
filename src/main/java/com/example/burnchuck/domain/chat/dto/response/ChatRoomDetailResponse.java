package com.example.burnchuck.domain.chat.dto.response;

import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.enums.RoomType;
import com.example.burnchuck.domain.chat.dto.dto.ChatRoomMemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter

@AllArgsConstructor
public class ChatRoomDetailResponse {

    private final Long roomId;
    private final String roomName;
    private final RoomType roomType;
    private final int memberCount;
    private final Long meetingId;
    private final List<ChatRoomMemberDto> members;

    public static ChatRoomDetailResponse from(ChatRoom room, String roomName, List<ChatRoomMemberDto> members) {
        return new ChatRoomDetailResponse(
                room.getId(),
                roomName,
                room.getType(),
                members.size(),
                room.getMeetingId(),
                members
        );
    }
}