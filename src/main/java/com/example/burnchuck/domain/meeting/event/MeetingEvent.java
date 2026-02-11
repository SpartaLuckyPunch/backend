package com.example.burnchuck.domain.meeting.event;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingTaskType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingEvent {

    private MeetingTaskType type;
    private Meeting meeting;
}
