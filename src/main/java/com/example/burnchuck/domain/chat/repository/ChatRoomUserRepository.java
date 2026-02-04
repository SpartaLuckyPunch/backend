package com.example.burnchuck.domain.chat.repository;

import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.entity.ChatRoomUser;
import com.example.burnchuck.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    @Query("SELECT cru FROM ChatRoomUser cru " +
            "JOIN FETCH cru.chatRoom cr " +
            "WHERE cru.user.id = :userId " +
            "AND cru.isDeleted = false")
    List<ChatRoomUser> findAllActiveByUserId(@Param("userId") Long userId);

    int countByChatRoomId(Long id);

    Optional<ChatRoomUser> findByChatRoomIdAndUserId(Long roomId, Long id);

    List<ChatRoomUser> findByChatRoomId(Long chatRoomId);

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
}
