package com.example.burnchuck.domain.meeting.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.ATTENDANCE_CANCEL_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.ATTENDANCE_GET_MEETING_LIST_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.ATTENDANCE_REGISTER_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.domain.meeting.dto.response.AttendanceGetMeetingListResponse;
import com.example.burnchuck.domain.meeting.service.AttendanceService;
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
@RequestMapping("/api/meetings")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 모임 참여 신청
     */
    @PostMapping("/{meetingId}/attendance")
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
    @DeleteMapping("/{meetingId}/attendance")
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
    @GetMapping("/attendance-meetings")
    public ResponseEntity<CommonResponse<AttendanceGetMeetingListResponse>> getAttendingMeetingList(
        @AuthenticationPrincipal AuthUser authUser
    ) {
        AttendanceGetMeetingListResponse response = attendanceService.getAttendingMeetingList(authUser);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(ATTENDANCE_GET_MEETING_LIST_SUCCESS, response));
    }
}
