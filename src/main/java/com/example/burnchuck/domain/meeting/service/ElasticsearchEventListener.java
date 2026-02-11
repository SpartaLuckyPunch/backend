package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingTaskType;
import com.example.burnchuck.domain.meeting.event.MeetingEvent;
import com.example.burnchuck.domain.meeting.event.MeetingStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ElasticsearchEventListener {

    private final ElasticsearchService elasticsearchService;

    @Async("CustomTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void meetingSyncElasticsearch(MeetingEvent event) {

        MeetingTaskType type = event.getType();
        Meeting meeting = event.getMeeting();

        switch (type) {
            case CREATE -> elasticsearchService.saveMeeting(meeting);
            case UPDATE -> elasticsearchService.updateMeeting(meeting);
            case DELETE -> elasticsearchService.deleteMeeting(meeting);
        }
    }

    @Async("CustomTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void meetingChangeStatus(MeetingStatusChangeEvent event) {

        elasticsearchService.updateMeetingStatus(event.getMeetingId(), event.getStatus());
    }
}
