package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.domain.notification.repository.EmitterRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "sseHeartbeat")
public class SseHeartbeatScheduler {

    private final EmitterRepository emitterRepository;

    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() {

        Map<String, SseEmitter> emitters = emitterRepository.findAll();

        log.info("현재 SSE 연결 수 = {}", emitters.size());

        if (emitters.isEmpty()) return;

        List<String> deadEmitters = new ArrayList<>();

        emitters.forEach((memberId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("heartbeat")
                    .data("ping"));
            } catch (Exception e) {
                deadEmitters.add(memberId);
            }
        });

        deadEmitters.forEach(emitterRepository::deleteById);
    }
}
