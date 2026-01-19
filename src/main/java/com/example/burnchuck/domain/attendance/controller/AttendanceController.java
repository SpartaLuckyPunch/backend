package com.example.burnchuck.domain.attendance.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.ATTENDANCE_REGISTER_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.attendance.service.AttendanceService;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
