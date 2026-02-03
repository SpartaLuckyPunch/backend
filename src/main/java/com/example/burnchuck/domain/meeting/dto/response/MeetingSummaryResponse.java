package com.example.burnchuck.domain.meeting.dto.response;

import com.example.burnchuck.common.entity.Meeting;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MeetingSummaryResponse {

    private final Long meetingId;
    private final String meetingTitle;
    private final String imgUrl;
    private final String location;
    private final Double latitude;
    private final Double longitude;
    private final LocalDateTime meetingDatetime;
    private final String status;
    private final int maxAttendees;
    private final int currentAttendees;

    public static MeetingSummaryResponse from(Meeting meeting, int currentAttendees) {
        return new MeetingSummaryResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getImgUrl(),
                meeting.getLocation(),
                meeting.getLatitude(),
                meeting.getLongitude(),
                meeting.getMeetingDateTime(),
                meeting.getStatus().name(),
                meeting.getMaxAttendees(),
                currentAttendees
        );
    }
}
