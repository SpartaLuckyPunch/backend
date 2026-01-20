package com.example.burnchuck.domain.notification.model.response;

import com.example.burnchuck.common.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class NotificationResponse {

    private final Long notificationId;
    private final String type;
    private final String description;
    private final LocalDateTime notificatedDatetime;
    private final Long meetingId;
    private final boolean check;

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
}
