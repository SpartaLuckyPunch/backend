package com.example.burnchuck.domain.chat.repository;

import static com.example.burnchuck.common.enums.ErrorCode.CHAT_USER_NOT_FOUND;

import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.entity.ChatRoomUser;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    @Query("""
            SELECT cru
            FROM ChatRoomUser cru
            JOIN FETCH cru.chatRoom cr
            WHERE cru.user.id = :userId
            AND cru.isDeleted = false
            """)
    List<ChatRoomUser> findAllActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT cru.user.id FROM ChatRoomUser cru WHERE cru.chatRoom.id = :chatRoomId AND cru.isDeleted = false")
    List<Long> findAllActiveUserIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    int countByChatRoomId(Long id);

    @Query("""
            SELECT cru
            FROM ChatRoomUser cru
            JOIN FETCH cru.chatRoom cr
            WHERE cru.chatRoom.id = :roomId AND cru.user.id = :userId
            AND cru.isDeleted = false
            """)
    Optional<ChatRoomUser> findByChatRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    List<ChatRoomUser> findByChatRoomId(Long chatRoomId);

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    default ChatRoomUser findChatRoomUserByChatRoomIdAndUserId(Long roomId, Long id) {
        return findByChatRoomIdAndUserId(roomId, id)
            .orElseThrow(() -> new CustomException(CHAT_USER_NOT_FOUND));
    }
}
