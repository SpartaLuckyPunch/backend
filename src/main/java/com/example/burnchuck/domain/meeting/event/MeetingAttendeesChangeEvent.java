package com.example.burnchuck.domain.meeting.event;

import com.example.burnchuck.common.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingAttendeesChangeEvent {

    private Meeting meeting;
}
