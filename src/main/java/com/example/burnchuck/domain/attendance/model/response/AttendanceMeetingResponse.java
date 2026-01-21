package com.example.burnchuck.domain.attendance.model.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceMeetingResponse {

    private final Long meetingId;
    private final String meetingTitle;
    private final String imgUrl;
    private final String location;
    private final LocalDateTime meetingDatetime;
    private final String status;
    private final int maxAttendees;
    private final int currentAttendees;
}
