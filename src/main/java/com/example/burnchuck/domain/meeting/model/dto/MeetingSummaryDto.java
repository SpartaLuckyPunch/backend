package com.example.burnchuck.domain.meeting.model.dto;

import com.example.burnchuck.common.entity.Meeting;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MeetingSummaryDto {

    private final Long meetingId;
    private final String meetingTitle;
    private final String imgUrl;
    private final String location;
    private final Double latitude;
    private final Double longitude;
    private final LocalDateTime meetingDatetime;
    private final int maxAttendees;
    private final int currentAttendees;

    public static MeetingSummaryDto from(Meeting meeting, int currentAttendees) {
        return new MeetingSummaryDto(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getImgUrl(),
                meeting.getLocation(),
                meeting.getLatitude(),
                meeting.getLongitude(),
                meeting.getMeetingDateTime(),
                meeting.getMaxAttendees(),
                currentAttendees
        );
    }
}
