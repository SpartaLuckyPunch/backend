package com.example.burnchuck.domain.attendance.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MeetingMemberResponse {

    private final Long meetingId;
    private final List<MeetingMemberDto> members;

    @Getter
    @AllArgsConstructor
    public static class MeetingMemberDto {
        private Long userId;
    }
}
