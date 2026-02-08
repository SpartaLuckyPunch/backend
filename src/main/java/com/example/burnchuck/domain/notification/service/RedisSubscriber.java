package com.example.burnchuck.domain.notification.service;

import static com.example.burnchuck.domain.notification.service.RedisMessageService.CHANNEL_PREFIX;

import com.example.burnchuck.domain.notification.dto.response.NotificationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j(topic = "RedisSubscriber")
@RequiredArgsConstructor
@Component
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseNotifyService sseNotifyService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            String channel = new String(message.getChannel()).substring(CHANNEL_PREFIX.length());
            Long userId = Long.parseLong(channel);

            NotificationResponse notification = objectMapper.readValue(message.getBody(), NotificationResponse.class);

            sseNotifyService.sendNotification(userId, notification);
        } catch (IOException e) {
            log.error("알림 전송 실패: {}", e.getMessage());
        }
    }
}
