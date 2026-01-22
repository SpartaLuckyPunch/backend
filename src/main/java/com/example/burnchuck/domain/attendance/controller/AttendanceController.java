package com.example.burnchuck.domain.attendance.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.ATTENDANCE_CANCEL_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.ATTENDANCE_GET_MEETING_LIST_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.ATTENDANCE_REGISTER_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_GET_MEMBER_LIST_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.attendance.model.response.AttendanceGetMeetingListResponse;
import com.example.burnchuck.domain.attendance.model.response.MeetingMemberResponse;
import com.example.burnchuck.domain.attendance.service.AttendanceService;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 모임 참여 신청
     */
    @PostMapping("/meetings/{meetingId}/attendance")
    public ResponseEntity<CommonResponse<Void>> registerAttendance(
        @AuthenticationPrincipal AuthUser authUser,
        @PathVariable Long meetingId
    ) {
        attendanceService.registerAttendance(authUser, meetingId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.successNodata(ATTENDANCE_REGISTER_SUCCESS));
    }

    /**
     * 모임 참여 취소
     */
    @DeleteMapping("/meetings/{meetingId}/attendance")
    public ResponseEntity<CommonResponse<Void>> cancelAttendance(
        @AuthenticationPrincipal AuthUser authUser,
        @PathVariable Long meetingId
    ) {
        attendanceService.cancelAttendance(authUser, meetingId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.successNodata(ATTENDANCE_CANCEL_SUCCESS));
    }

    /**
     * 참여 중인 모임 목록 조회
     */
    @GetMapping("/meetings/attendance-meetings")
    public ResponseEntity<CommonResponse<AttendanceGetMeetingListResponse>> getAttendingMeetingList(
        @AuthenticationPrincipal AuthUser authUser
    ) {
        AttendanceGetMeetingListResponse response = attendanceService.getAttendingMeetingList(authUser);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(ATTENDANCE_GET_MEETING_LIST_SUCCESS, response));
    }

    /**
     * 모임 참여자 목록 조회
     */
    @GetMapping("/meetings/{meetingId}/attendees")
    public ResponseEntity<CommonResponse<MeetingMemberResponse>> getMeetingMembers(
            @PathVariable Long meetingId
    ) {
        MeetingMemberResponse response = attendanceService.getMeetingMembers(meetingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(MEETING_GET_MEMBER_LIST_SUCCESS, response));
    }
}
