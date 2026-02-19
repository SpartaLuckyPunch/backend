package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.domain.notification.dto.response.NotificationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisMessageService {

    public static final String CHANNEL_PREFIX = "channel:userId:";

    private final RedisMessageListenerContainer container;
    private final RedisSubscriber subscriber;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 채널 구독
     */
    public void subscribe(Long userId) {
        container.addMessageListener(subscriber, ChannelTopic.of(generateChannelName(userId)));
    }

    /**
     * 이벤트 발행
     */
    public void publish(Long userId, NotificationResponse notification) {
        try {
            String message = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend(generateChannelName(userId), message);
        } catch (JsonProcessingException e) {
            log.error("알림 생성 실패: {}", e.getMessage());
        }
    }

    /**
     * 구독 삭제
     */
    public void removeSubscribe(Long userId) {
        container.removeMessageListener(subscriber, ChannelTopic.of(generateChannelName(userId)));
    }

    /**
     * 채널명 : channel:userId:{userId}
     */
    private String generateChannelName(Long userId) {
        return CHANNEL_PREFIX + userId;
    }
}
