package com.example.burnchuck.domain.scheduler.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.domain.scheduler.dto.MeetingCreatedEvent;
import com.example.burnchuck.domain.scheduler.dto.MeetingDeletedEvent;
import com.example.burnchuck.domain.scheduler.dto.MeetingUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final ApplicationEventPublisher publisher;

    public void publishMeetingCreatedEvent(Meeting meeting) {

        MeetingCreatedEvent event = new MeetingCreatedEvent(meeting);
        publisher.publishEvent(event);
    }

    public void publishMeetingUpdatedEvent(Meeting meeting) {

        MeetingUpdatedEvent event = new MeetingUpdatedEvent(meeting);
        publisher.publishEvent(event);
    }

    public void publishMeetingDeletedEvent(Meeting meeting) {

        MeetingDeletedEvent event = new MeetingDeletedEvent(meeting);
        publisher.publishEvent(event);
    }
}
