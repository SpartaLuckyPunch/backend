package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingTaskType;
import com.example.burnchuck.domain.scheduler.dto.MeetingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ElasticsearchEventListener {

    private final ElasticSearchService elasticSearchService;

    @Async("CustomTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void meetingSyncElasticsearch(MeetingEvent event) {

        MeetingTaskType type = event.getType();
        Meeting meeting = event.getMeeting();

        switch (type) {
            case CREATE, UPDATE -> elasticSearchService.saveMeeting(meeting);
            case DELETE -> elasticSearchService.deleteMeeting(meeting);
        }
    }
}
