package com.example.burnchuck.domain.meeting.dto.request;

import com.example.burnchuck.common.enums.MeetingSortOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSearchRequest {

    private String keyword;
    private Double distance;
    private MeetingSortOption order;
    private String category;
}
