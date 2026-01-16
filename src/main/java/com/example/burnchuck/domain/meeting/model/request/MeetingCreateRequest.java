package com.example.burnchuck.domain.meeting.model.request;

import com.example.burnchuck.common.entity.Category;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetingCreateRequest {

    private String title;
    private String description;
    private String imgUrl;
    private String location;
    private int maxAttendees;
    private LocalDateTime meetingDateTime;
    private Category category;
}
