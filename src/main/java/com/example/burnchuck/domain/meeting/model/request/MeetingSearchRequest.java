package com.example.burnchuck.domain.meeting.model.request;

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
    private String order;      // 정렬 기준: "LATEST"(최신순), "POPULAR"(인기순)
    private String category;
}
