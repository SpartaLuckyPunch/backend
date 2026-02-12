package com.example.burnchuck.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ws://localhost:8080/ws-stomp 로 연결
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*") // 모든 출처 허용 (보안 필요시 수정)
                .withSockJS(); // SockJS 지원
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독(수신) 경로 prefix
        registry.enableSimpleBroker("/sub");
        // 발행(송신) 경로 prefix
        registry.setApplicationDestinationPrefixes("/pub");
    }
}
