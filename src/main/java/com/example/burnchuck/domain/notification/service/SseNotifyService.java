package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.domain.notification.dto.response.NotificationResponse;
import com.example.burnchuck.domain.notification.dto.response.NotificationSseResponse;
import com.example.burnchuck.domain.notification.repository.NotificationRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseNotifyService {

    private final NotificationRepository notificationRepository;
    private final EmitterService emitterService;

    /**
     * 알림 이벤트 전송
     */
    public void send(SseEmitter emitter, Long userId, Object data) {

        try {
            emitter.send(SseEmitter.event()
                .name("sse")
                .data(data));

        } catch (IOException exception) {
            emitterService.deleteEmitter(userId);
        }
    }

    public void sendNotification(Long userId, NotificationResponse notification) {

        Map<String, SseEmitter> emitters = emitterService.findAllEmittersByUserId(userId);

        LocalDateTime sevenDaysAgo = LocalDate.now().atStartOfDay().minusDays(7);
        long unread = notificationRepository.countUnReadNotificationsInSevenDaysByUserId(userId, sevenDaysAgo);

        emitters.forEach(
            (key, emitter) -> send(emitter, userId, NotificationSseResponse.newNotification(unread, notification))
        );
    }
}
