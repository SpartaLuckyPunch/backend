package com.example.burnchuck.common.entity;

import com.example.burnchuck.common.enums.RoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Column(name = "meeting_id")
    private Long meetingId;

    public ChatRoom(String name, RoomType type, Long meetingId) {
        this.name = name;
        this.type = type;
        this.meetingId = meetingId;
    }

    // 1:1 채팅룸 만들 시 필요
    public ChatRoom(String name, RoomType type) {
        this.name = name;
        this.type = type;
        this.meetingId = null;
    }

    public boolean isPrivate() {
        return this.type == RoomType.PRIVATE;
    }

    public boolean isGroup() {
        return this.type == RoomType.GROUP;
    }
}