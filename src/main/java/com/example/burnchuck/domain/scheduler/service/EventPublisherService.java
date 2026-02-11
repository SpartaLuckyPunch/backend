package com.example.burnchuck.domain.scheduler.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingTaskType;
import com.example.burnchuck.domain.scheduler.dto.MeetingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final ApplicationEventPublisher publisher;

    public void publishMeetingCreatedEvent(Meeting meeting) {

        MeetingEvent event = new MeetingEvent(MeetingTaskType.CREATE, meeting);
        publisher.publishEvent(event);
    }

    public void publishMeetingUpdatedEvent(Meeting meeting) {

        MeetingEvent event = new MeetingEvent(MeetingTaskType.UPDATE, meeting);
        publisher.publishEvent(event);
    }

    public void publishMeetingDeletedEvent(Meeting meeting) {

        MeetingEvent event = new MeetingEvent(MeetingTaskType.DELETE, meeting);
        publisher.publishEvent(event);
    }
}
