package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.common.entity.Notification;
import com.example.burnchuck.domain.notification.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 생성 및 저장
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createNotification(List<Notification> notification) {

        notificationRepository.saveAll(notification);
    }
}
