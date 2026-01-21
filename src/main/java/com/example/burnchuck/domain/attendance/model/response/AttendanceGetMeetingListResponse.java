package com.example.burnchuck.domain.attendance.model.response;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceGetMeetingListResponse {

    private final List<AttendanceMeetingResponse> meetingList;
}
