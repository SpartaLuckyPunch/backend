package com.example.burnchuck.domain.notification.repository;

import com.example.burnchuck.common.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
