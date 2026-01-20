package com.example.burnchuck.domain.meeting.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

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
    private final long likes;
    private final long views;
}
