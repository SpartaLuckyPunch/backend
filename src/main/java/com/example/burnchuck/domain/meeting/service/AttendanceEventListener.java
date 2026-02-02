package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AttendanceEventListener {
    private final NotificationService notificationService;
    private final ChatRoomService chatRoomService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAttendanceSuccess(AttendanceServiceEvent event) {
        // event 객체에서 meeting과 user를 꺼내와야 합니다.
        // 만약 record로 만드셨다면 event.meeting() / 클래스라면 event.getMeeting()
        chatRoomService.joinGroupChatRoom(event.getMeeting().getId(), event.getUser());
        notificationService.notifyMeetingMember(NotificationType.MEETING_MEMBER_JOIN, event.getMeeting(), event.getUser());
    }
}