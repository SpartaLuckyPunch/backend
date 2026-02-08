package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.domain.notification.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisMessageService {

    public static final String CHANNEL_PREFIX = "channel:userId:";

    private final RedisMessageListenerContainer container;
    private final RedisSubscriber subscriber;
    private final RedisTemplate<String, Object> redisTemplate;

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
        redisTemplate.convertAndSend(generateChannelName(userId), notification);
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
