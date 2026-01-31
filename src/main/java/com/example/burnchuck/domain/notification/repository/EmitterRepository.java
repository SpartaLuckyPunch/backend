package com.example.burnchuck.domain.notification.repository;

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
    public static final String EVENT_CACHE_PREFIX = "Event Cache::";

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    /**
     * emitter 저장
     */
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {

        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    /**
     * 이벤트 저장
     */
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    /**
     * 해당 회원과 관련된 모든 emitter 조회
     */
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(Long userId) {

        String key = SSE_EMITTER_PREFIX + userId + "_";

        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(key))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 해당 회원과 관련된 모든 이벤트 조회
     */
    public Map<String, Object> findAllEventCacheStartWithByMemberId(Long userId) {

        String key = EVENT_CACHE_PREFIX + userId + "_";

        return eventCache.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(key))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * emitter 단건 삭제
     */
    public void deleteById(String emitterId) {

        emitters.remove(emitterId);
    }

    /**
     * 해당 회원과 관련된 모든 emitter 삭제
     */
    public void deleteAllEmitterStartWithId(Long userId) {

        String userKey = SSE_EMITTER_PREFIX + userId + "_";

        emitters.forEach(
            (key, emitter) -> {
                if (key.startsWith(userKey)) {
                    emitters.remove(key);
                }
            }
        );
    }

    /**
     * 해당 회원과 관련된 모든 이벤트 삭제
     */
    public void deleteAllEventCacheStartWithId(Long userId) {

        String userKey = EVENT_CACHE_PREFIX + userId + "_";

        eventCache.forEach(
            (key, emitter) -> {
                if (key.startsWith(userKey)) {
                    eventCache.remove(key);
                }
            }
        );
    }
}
