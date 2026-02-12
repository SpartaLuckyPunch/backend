package com.example.burnchuck.domain.chat.repository;

import static com.example.burnchuck.common.enums.ErrorCode.CHAT_ROOM_NOT_FOUND;

import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
          SELECT r FROM ChatRoom r
          JOIN ChatRoomUser u1 ON r.id = u1.chatRoom.id
          JOIN ChatRoomUser u2 ON r.id = u2.chatRoom.id
          WHERE r.type = 'PRIVATE'
          AND u1.user.id = :userId1 AND u2.user.id = :userId2
          """)
    Optional<ChatRoom> findPrivateChatRoom(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    Optional<ChatRoom> findByMeetingId(Long meetingId);

    default ChatRoom findChatRoomByMeetingId(Long meetingId) {
        return findByMeetingId(meetingId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));
    }
}
