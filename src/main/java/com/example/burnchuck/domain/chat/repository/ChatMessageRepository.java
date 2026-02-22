package com.example.burnchuck.domain.chat.repository;

import com.example.burnchuck.common.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // 채팅 내역 페이징 조회 (최신순)
    Slice<ChatMessage> findByRoomIdOrderByCreatedDatetimeDesc(Long roomId, Pageable pageable);

    // 채팅방 목록에서 보여줄 마지막 메시지 1개 조회용
    Optional<ChatMessage> findFirstByRoomIdOrderByCreatedDatetimeDesc(Long roomId);
}
