package com.example.burnchuck.domain.meeting.event;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingStatusChangeEvent {

    private Meeting meeting;
    private MeetingStatus status;
}
