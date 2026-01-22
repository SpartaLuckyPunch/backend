package com.example.burnchuck.domain.notification.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationGetListResponse {

    private final List<NotificationResponse> notificationList;
}
