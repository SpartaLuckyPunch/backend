package com.example.burnchuck.domain.meeting.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HostedMeetingResponse {

    private Long meetingId;
    private String meetingTitle;
    private String imgUrl;
    private String location;
    private LocalDateTime meetingDatetime;
    private String status;
    private int maxAttendees;
    private Long currentAttendees;
}
