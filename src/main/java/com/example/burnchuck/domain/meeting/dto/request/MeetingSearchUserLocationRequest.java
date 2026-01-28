package com.example.burnchuck.domain.meeting.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSearchUserLocationRequest {

    private Double latitude;
    private Double longitude;
    private Double distance;
}
