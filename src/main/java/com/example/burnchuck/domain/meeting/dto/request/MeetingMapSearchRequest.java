package com.example.burnchuck.domain.meeting.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingMapSearchRequest {

    private String keyword;
    private String category;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
}
