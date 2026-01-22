package com.example.burnchuck.domain.meeting.model.response;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceGetMeetingListResponse {

    private final List<MeetingSummaryWithStatusResponse> meetingList;
}
