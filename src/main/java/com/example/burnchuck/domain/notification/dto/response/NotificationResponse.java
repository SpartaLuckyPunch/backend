package com.example.burnchuck.domain.notification.dto.response;

import com.example.burnchuck.common.entity.Notification;
import com.example.burnchuck.common.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long notificationId;
    private String type;
    private String description;
    private LocalDateTime notificatedDatetime;
    private Long meetingId;
    private boolean check;

    public NotificationResponse(
        Long notificationId,
        NotificationType type,
        String description,
        LocalDateTime notificatedDatetime,
        Long meetingId,
        boolean check
    ) {
        this.notificationId = notificationId;
        this.type = type.toString();
        this.description = description;
        this.notificatedDatetime = notificatedDatetime;
        this.meetingId = meetingId;
        this.check = check;
    }

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getType().toString(),
            notification.getDescription(),
            notification.getNotifiedDatetime(),
            notification.getMeeting().getId(),
            notification.isRead()
        );
    }
}
