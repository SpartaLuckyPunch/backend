package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.common.enums.MeetingTaskType;
import com.example.burnchuck.domain.meeting.event.MeetingAttendeesChangeEvent;
import com.example.burnchuck.domain.meeting.event.MeetingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async("customTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void createNewFollowerPostNotification(MeetingEvent event) {

        MeetingTaskType type = event.getType();

        if (type == MeetingTaskType.CREATE) {
            notificationService.notifyNewFollowerPost(event.getMeeting());
        }
    }

    @Async("customTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void createMeetingMemberNotification(MeetingAttendeesChangeEvent event) {

        notificationService.notifyMeetingMember(event.getType(), event.getMeeting(), event.getUser());
    }
}
