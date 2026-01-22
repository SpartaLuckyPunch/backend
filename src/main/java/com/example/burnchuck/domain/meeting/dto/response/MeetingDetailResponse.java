package com.example.burnchuck.domain.meeting.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MeetingDetailResponse {

    private final Long meetingId;
    private final String meetingTitle;
    private final String imgUrl;
    private final String description;
    private final String location;
    private final Double latitude;
    private final Double longitude;
    private final LocalDateTime meetingDatetime;
    private final int maxAttendees;
    private final int currentAttendees;
    private final String meetingStatus;
    private final long likes;
    private final long views;
}
