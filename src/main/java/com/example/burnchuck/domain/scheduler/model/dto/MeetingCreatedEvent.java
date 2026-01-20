package com.example.burnchuck.domain.scheduler.model.dto;

import com.example.burnchuck.common.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingCreatedEvent {

    private Meeting meeting;
}
