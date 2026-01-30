package com.example.burnchuck.domain.chat.repository;

import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.entity.ChatRoomUser;
import com.example.burnchuck.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    List<ChatRoomUser> findAllByUserId(Long id);

    int countByChatRoomId(Long id);

    Optional<ChatRoomUser> findByChatRoomIdAndUserId(Long roomId, Long id);

    List<ChatRoomUser> findByChatRoomId(Long chatRoomId);

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
}
