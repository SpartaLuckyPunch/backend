package com.example.burnchuck.domain.attendance.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendeeResponse {

    private Long attendeeId;
    private String attendeeProfileImgUrl;
    private String attendeeNickname;
}
