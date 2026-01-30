package com.example.burnchuck.common.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private String id;

    @Field("room_id")
    private Long roomId;

    @Field("sender_id")
    private Long senderId;

    private String content;

    @Field("sender_nickname")
    private String senderNickname;

    @Field("sender_profile")
    private String senderProfile;

    @CreatedDate
    private LocalDateTime createdDatetime;

    public ChatMessage(Long roomId, Long senderId, String content, String senderNickname, String senderProfile) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.senderNickname = senderNickname;
        this.senderProfile = senderProfile;
    }
}
