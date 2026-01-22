package com.example.burnchuck.domain.meeting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendeeResponse {

    private Long attendeeId;
    private String attendeeProfileImgUrl;
    private String attendeeNickname;
}
