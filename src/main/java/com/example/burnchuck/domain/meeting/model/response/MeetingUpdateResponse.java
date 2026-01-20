package com.example.burnchuck.domain.meeting.model.response;

import com.example.burnchuck.common.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MeetingUpdateResponse {

    private Long meetingId;
    private String title;
    private String imgUrl;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime meetingDateTime;

    public static MeetingUpdateResponse from(Meeting meeting) {
        return new MeetingUpdateResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getImgUrl(),
                meeting.getDescription(),
                meeting.getLocation(),
                meeting.getLatitude(),
                meeting.getLongitude(),
                meeting.getMeetingDateTime()
        );
    }
}

