package com.example.burnchuck.domain.notification.service;

import static com.example.burnchuck.domain.notification.repository.EmitterRepository.SSE_EMITTER_PREFIX;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Notification;
import com.example.burnchuck.domain.notification.dto.response.NotificationResponse;
import com.example.burnchuck.domain.notification.dto.response.NotificationSseResponse;
import com.example.burnchuck.domain.notification.repository.EmitterRepository;
import com.example.burnchuck.domain.notification.repository.NotificationRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseNotifyService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    /**
     * 클라이언트와의 SSE 스트림 통신 연결(성공 시, EventStream Created. [userId="{userId}"] 반환)
     */
    public SseEmitter subscribe(AuthUser authUser) {

        Long userId = authUser.getId();

        String emitterId = createEmitterId(userId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        long unread = notificationRepository.countByUserIdAndIsReadFalse(userId);

        sendNotification(emitter, emitterId, NotificationSseResponse.sseConnection(unread));

        return emitter;
    }

    /**
     * Emitter ID 생성(prefix::userId_현재시간)
     */
    private String createEmitterId(Long userId) {
        return SSE_EMITTER_PREFIX + userId + "_" + System.currentTimeMillis();
    }

    /**
     * 알림 이벤트 전송
     */
    private void sendNotification(SseEmitter emitter, String emitterId, Object data) {

        try {
            emitter.send(SseEmitter.event()
                .name("sse")
                .data(data));

        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    /**
     * 해당 알림을 수신하는 모든 유저에게 전송
     */
    public void send(Notification notification) {

        Long userId = notification.getUser().getId();

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(userId);

        NotificationResponse notificationResponse = NotificationResponse.from(notification);
        long unread = notificationRepository.countByUserIdAndIsReadFalse(userId);

        emitters.forEach(
            (key, emitter) -> sendNotification(emitter, key, NotificationSseResponse.newNotification(unread, notificationResponse))
        );
    }

    public void sendAll(List<Notification> notificationList) {

        for (Notification notification : notificationList) {

            Long userId = notification.getUser().getId();

            Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(userId);

            NotificationResponse notificationResponse = NotificationResponse.from(notification);
            long unread = notificationRepository.countByUserIdAndIsReadFalse(userId);

            emitters.forEach(
                (key, emitter) -> {
                    sendNotification(emitter, key, NotificationSseResponse.newNotification(unread, notificationResponse));
                }
            );
        }
    }
}
