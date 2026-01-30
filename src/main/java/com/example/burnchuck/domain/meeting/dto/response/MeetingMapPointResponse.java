package com.example.burnchuck.domain.meeting.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MeetingMapPointResponse {

    private final Long meetingId;
    private final String meetingTitle;
    private final Double latitude;
    private final Double longitude;
}
