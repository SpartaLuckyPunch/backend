package com.example.burnchuck.domain.chat.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.*;
import com.example.burnchuck.domain.chat.dto.request.ChatMessageRequest;
import com.example.burnchuck.domain.chat.dto.response.ChatMessageResponse;
import com.example.burnchuck.domain.chat.repository.ChatMessageRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅 보내기
     */
    @Transactional
    public ChatMessageResponse sendMessage(AuthUser authUser, Long roomId, ChatMessageRequest request) {

        User sender = userRepository.findActivateUserById(authUser.getId());

        ChatMessage chatMessage = new ChatMessage(
                roomId,
                sender.getId(),
                request.getContent(),
                sender.getNickname(),
                sender.getProfileImgUrl()
        );

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);

        messagingTemplate.convertAndSend("/sub/chat/room/" + response.getRoomId(), response);

        return response;
    }
}
