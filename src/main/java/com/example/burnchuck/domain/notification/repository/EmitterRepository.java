package com.example.burnchuck.domain.notification.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@NoArgsConstructor
public class EmitterRepository {

    public static final String SSE_EMITTER_PREFIX = "SSE Emitters::";

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * emitter 저장
     */
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {

        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    /**
     * emitter 조회
     */
    public Map<String, SseEmitter> findAllEmitterStartWith(String emitterId) {

        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(emitterId))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, SseEmitter> findAll() {
        return emitters.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * emitter 단건 삭제
     */
    public void deleteById(String emitterId) {

        SseEmitter emitter = emitters.remove(emitterId);

        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        }
    }

    public void disconnectAllEmittersStartWith(String emitterIdPrefix) {

        List<String> keys = emitters.keySet().stream()
            .filter(k -> k.startsWith(emitterIdPrefix))
            .toList();

        for (String key : keys) {
            deleteById(key);
        }
    }
}
