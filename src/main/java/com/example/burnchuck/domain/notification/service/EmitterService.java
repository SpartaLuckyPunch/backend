package com.example.burnchuck.domain.notification.service;

import static com.example.burnchuck.domain.notification.repository.EmitterRepository.SSE_EMITTER_PREFIX;

import com.example.burnchuck.domain.notification.repository.EmitterRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class EmitterService {

    private static final Long DEFAULT_TIMEOUT = 30L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    /**
     * SseEmitter 객체 생성 및 저장
     */
    public SseEmitter createEmitter(Long userId) {

        String emitterId = generateEmitterId(userId);
        return emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
    }

    /**
     * emitter 삭제
     */
    public void deleteEmitter(Long userId) {

        String emitterId = generateEmitterId(userId);
        emitterRepository.deleteById(emitterId);
    }

    /**
     * 해당 회원과 관련된 모든 emitter 조회
     */
    public Map<String, SseEmitter> findAllEmittersByUserId(Long userId) {

        String emitterId = generateEmitterIdExceptTime(userId);
        return emitterRepository.findAllEmitterStartWith(emitterId);
    }

    /**
     * 해당 회원과 관련된 모든 emitter 종료 후 삭제
     */
    public void disconnectAllEmittersByUserId(Long userId) {

        String emitterId = generateEmitterIdExceptTime(userId);
        emitterRepository.disconnectAllEmittersStartWith(emitterId);
    }

    /**
     * Emitter ID 생성(prefix::userId_현재시간)
     */
    private String generateEmitterId(Long userId) {
        return SSE_EMITTER_PREFIX + userId + "_" + System.currentTimeMillis();
    }

    /**
     * Emitter ID 생성(prefix::userId_)
     */
    private String generateEmitterIdExceptTime(Long userId) {
        return SSE_EMITTER_PREFIX + userId + "_";
    }
}
