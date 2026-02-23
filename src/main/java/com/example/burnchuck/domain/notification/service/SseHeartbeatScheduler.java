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
@Slf4j(topic = "sseHeartBeat")
public class SseHeartbeatScheduler {

    private final EmitterRepository emitterRepository;

    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() {

        Map<String, SseEmitter> emitters = emitterRepository.findAll();

        if (emitters.isEmpty()) {
            return;
        }

        if (emitters.size() > 100) {
            log.info("SSE 개수: {}", emitters.size());
        }

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
