package com.example.burnchuck.domain.notification.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationSseResponse {

    private final NotificationEvent event;
    private final Long unread;
    private final NotificationResponse newNotification;

    public enum NotificationEvent {
        CONNECTION_SUCCESS, NEW_NOTIFICATION;
    }

    public static NotificationSseResponse sseConnection(Long unread) {
        return new NotificationSseResponse(NotificationEvent.CONNECTION_SUCCESS, unread, null);
    }

    public static NotificationSseResponse newNotification(Long unread, NotificationResponse notification) {
        return new NotificationSseResponse(NotificationEvent.NEW_NOTIFICATION, unread, notification);
    }
}
