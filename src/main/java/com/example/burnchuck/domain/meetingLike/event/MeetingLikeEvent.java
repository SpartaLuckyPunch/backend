package com.example.burnchuck.domain.meetingLike.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingLikeEvent {

    private MeetingLikeEventType type;
    private Long meetingId;

    public enum MeetingLikeEventType {
        INCREASE, DECREASE
    }
}
