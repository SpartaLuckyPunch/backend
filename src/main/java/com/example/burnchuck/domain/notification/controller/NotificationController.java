package com.example.burnchuck.domain.notification.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.NOTIFICATION_GET_LIST_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.NOTIFICATION_GET_ONE_SUCCESS;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.notification.dto.response.NotificationGetListResponse;
import com.example.burnchuck.domain.notification.dto.response.NotificationResponse;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.notification.service.SseNotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "Notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseNotifyService sseNotifyService;

    /**
     * 알림 목록 조회 (로그인한 유저 기준)
     */
    @Operation(
            summary = "알림 목록 조회",
            description = """
                    토큰을 기준으로 해당 유저에게 도착한 알림 목록을 조회합니다.
                    """
    )
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
    @Operation(
            summary = "알림 단건 조회",
            description = """
                    알림 목록 중 특정 알림을 단건으로 조회합니다.
                    조회 시 해당 알림은 읽음 처리됩니다. (isRead)
                    """
    )
    @GetMapping("/{notificationId}")
    public ResponseEntity<CommonResponse<NotificationResponse>> readNotification(
        @PathVariable Long notificationId
    ) {
        NotificationResponse response = notificationService.readNotification(notificationId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(NOTIFICATION_GET_ONE_SUCCESS, response));
    }

    /**
     * 알림 구독 요청
     */
    @Operation(
        summary = "알림 단건 조회",
        description = """
                    알림을 구독합니다.
                    """
    )
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(
        @AuthenticationPrincipal AuthUser authUser,
        @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId
    ) {
        return sseNotifyService.subscribe(authUser, lastEventId);
    }
}
