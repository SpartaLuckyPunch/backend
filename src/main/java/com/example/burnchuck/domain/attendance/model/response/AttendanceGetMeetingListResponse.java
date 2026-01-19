package com.example.burnchuck.domain.attendance.model.response;

import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceGetMeetingListResponse {

    private final List<MeetingSummaryDto> meetingList;
}
