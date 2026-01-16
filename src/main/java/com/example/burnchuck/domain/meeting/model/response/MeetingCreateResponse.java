package com.example.burnchuck.domain.meeting.model.response;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Meeting;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class MeetingCreateResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String imgUrl;
    private final String location;
    private final int maxAttendees;
    private final LocalDateTime meetingDateTime;
    private final Category category;

    public static MeetingCreateResponse from(Meeting meeting) {
        return new MeetingCreateResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getDescription(),
                meeting.getImgUrl(),
                meeting.getLocation(),
                meeting.getMaxAttendees(),
                meeting.getMeetingDateTime(),
                meeting.getCategory()
        );
    }
}
