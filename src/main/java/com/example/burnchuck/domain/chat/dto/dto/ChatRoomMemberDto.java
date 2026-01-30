package com.example.burnchuck.domain.chat.dto.dto;

import com.example.burnchuck.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomMemberDto {
    private Long userId;
    private String nickname;
    private String profileImgUrl;

    public static ChatRoomMemberDto from(User user) {
        return new ChatRoomMemberDto(
                user.getId(),
                user.getNickname(),
                user.getProfileImgUrl()
        );
    }
}
