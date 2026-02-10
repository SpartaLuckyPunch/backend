package com.example.burnchuck.domain.meeting.dto.response;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingDocument;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingSummaryResponse {

    private Long meetingId;
    private String meetingTitle;
    private String imgUrl;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime meetingDatetime;
    private int maxAttendees;
    private int currentAttendees;

    public MeetingSummaryResponse(MeetingDocument meetingDocument) {
        this.meetingId = Long.parseLong(meetingDocument.getId());
        this.meetingTitle = meetingDocument.getTitle();
        this.imgUrl = meetingDocument.getImgUrl();
        this.location = meetingDocument.getLocation();
        this.latitude = meetingDocument.getGeoPoint().getLat();
        this.longitude = meetingDocument.getGeoPoint().getLon();
        this.meetingDatetime = meetingDocument.getMeetingDatetime();
        this.maxAttendees = meetingDocument.getMaxAttendees();
    }

    public static MeetingSummaryResponse from(Meeting meeting, int currentAttendees) {
        return new MeetingSummaryResponse(
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
