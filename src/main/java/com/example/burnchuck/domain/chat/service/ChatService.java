package com.example.burnchuck.domain.chat.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.*;
import com.example.burnchuck.domain.chat.dto.dto.ChatReadEvent;
import com.example.burnchuck.domain.chat.dto.request.ChatMessageRequest;
import com.example.burnchuck.domain.chat.dto.response.ChatMessageResponse;
import com.example.burnchuck.domain.chat.dto.response.ChatRoomUpdateEvent;
import com.example.burnchuck.domain.chat.repository.ChatMessageRepository;
import com.example.burnchuck.domain.chat.repository.ChatRoomUserRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatCacheService chatCacheService;

    /**
     * 채팅 보내기
     */
    @Transactional
    public ChatMessageResponse sendMessage(AuthUser authUser, Long roomId, ChatMessageRequest request) {

        User sender = userRepository.findActivateUserById(authUser.getId());

        Long msgSeq = chatCacheService.generateMessageSequence(roomId);

        ChatMessage chatMessage = new ChatMessage(
                roomId,
                sender.getId(),
                request.getContent(),
                sender.getNickname(),
                sender.getProfileImgUrl(),
                msgSeq
        );

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        chatCacheService.updateLastReadSequence(roomId, sender.getId(), msgSeq);

        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);

        messagingTemplate.convertAndSend("/sub/chat/room/" + response.getRoomId(), response);

        List<Long> memberIds = chatRoomUserRepository.findAllActiveUserIdByChatRoomId(roomId);
        ChatRoomUpdateEvent updateEvent = new ChatRoomUpdateEvent(
                roomId,
                savedMessage.getContent(),
                savedMessage.getCreatedDatetime()
        );

        for (Long memberId : memberIds) {
            messagingTemplate.convertAndSend("/sub/user/" + memberId + "/chat", updateEvent);
        }
        return response;
    }

    /**
     * 메시지 읽음 처리 (API 혹은 소켓 핸들러에서 호출)
     * 유저가 방에 들어오거나 스크롤 할 때 호출
     */
    public void readMessage(Long userId, Long roomId) {
        Long currentSeq = chatCacheService.getRoomCurrentSequence(roomId);
        chatCacheService.updateLastReadSequence(roomId, userId, currentSeq);

        ChatReadEvent readEvent = new ChatReadEvent(roomId, userId, currentSeq);
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId + "/read", readEvent);
    }
}
