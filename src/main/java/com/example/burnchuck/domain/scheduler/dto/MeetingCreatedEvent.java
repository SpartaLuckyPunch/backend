package com.example.burnchuck.domain.scheduler.dto;

import com.example.burnchuck.common.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingCreatedEvent {

    private Meeting meeting;
}
