package com.example.burnchuck.domain.notification.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.NOTIFICATION_GET_LIST_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.NOTIFICATION_GET_ONE_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.notification.model.response.NotificationGetListResponse;
import com.example.burnchuck.domain.notification.model.response.NotificationResponse;
import com.example.burnchuck.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 목록 조회 (로그인한 유저 기준)
     */
    @GetMapping
    public ResponseEntity<CommonResponse<NotificationGetListResponse>> getNotificationList(
        @AuthenticationPrincipal AuthUser authUser
    ) {
        NotificationGetListResponse response = notificationService.getNotificationList(authUser);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(NOTIFICATION_GET_LIST_SUCCESS, response));
    }

    /**
     * 알림 단건 조회 (알림 읽음 처리)
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<CommonResponse<NotificationResponse>> readNotification(
        @PathVariable Long notificationId
    ) {
        NotificationResponse response = notificationService.readNotification(notificationId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(NOTIFICATION_GET_ONE_SUCCESS, response));
    }
}
