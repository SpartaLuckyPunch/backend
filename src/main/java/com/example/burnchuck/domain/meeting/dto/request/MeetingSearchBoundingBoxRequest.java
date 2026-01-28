package com.example.burnchuck.domain.meeting.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSearchBoundingBoxRequest {

    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;
}
