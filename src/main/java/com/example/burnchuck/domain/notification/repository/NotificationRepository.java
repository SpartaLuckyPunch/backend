package com.example.burnchuck.domain.notification.repository;

import com.example.burnchuck.common.entity.Notification;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.notification.model.response.NotificationResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT new com.example.burnchuck.domain.notification.model.response.NotificationResponse(
                n.id,
                n.type,
                n.description,
                n.notifiedDatetime,
                n.meeting.id,
                n.isRead
              )
        FROM Notification n
        WHERE n.user = :user
        ORDER BY n.notifiedDatetime DESC
        """)
    List<NotificationResponse> findAllNotificationsByUser(@Param("user") User user);

    default Notification findNotificationById(Long notificationId) {
        return findById(notificationId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }
}
