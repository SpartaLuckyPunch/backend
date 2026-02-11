package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingTaskType;
import com.example.burnchuck.domain.meeting.event.MeetingAttendeesChangeEvent;
import com.example.burnchuck.domain.meeting.event.MeetingEvent;
import com.example.burnchuck.domain.meeting.event.MeetingStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ElasticsearchEventListener {

    private final ElasticsearchService elasticsearchService;

    @Async("CustomTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void meetingSyncElasticsearch(MeetingEvent event) {

        MeetingTaskType type = event.getType();
        Meeting meeting = event.getMeeting();

        switch (type) {
            case CREATE, UPDATE -> elasticsearchService.saveMeeting(meeting);
            case DELETE -> elasticsearchService.deleteMeeting(meeting);
        }
    }

    @Async("CustomTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void meetingChangeStatus(MeetingStatusChangeEvent event) {

        elasticsearchService.updateMeetingStatus(event.getMeetingId(), event.getStatus());
    }

    @Async("CustomTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void meetingChangeAttendees(MeetingAttendeesChangeEvent event) {

        elasticsearchService.updateMeetingCurrentAttendees(event.getMeeting());
    }
}
