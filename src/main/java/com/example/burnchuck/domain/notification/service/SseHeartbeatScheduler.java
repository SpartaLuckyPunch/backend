package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.domain.notification.repository.EmitterRepository;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class SseHeartbeatScheduler {

    private final EmitterRepository emitterRepository;

    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() {

        Map<String, SseEmitter> emitters = emitterRepository.findAll();

        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            String memberId = entry.getKey();
            SseEmitter emitter = entry.getValue();

            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IllegalStateException | IOException e) {
                emitterRepository.disconnectAllEmittersStartWith(memberId);
            }
        }
    }
}
