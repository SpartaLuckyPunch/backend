package com.example.burnchuck.domain.meeting.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meeting.model.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.model.response.MeetingCreateResponse;
import com.example.burnchuck.domain.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_CREATE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    /**
     * 모임 생성
     */
    @PostMapping
    public ResponseEntity<CommonResponse<MeetingCreateResponse>> createTask(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody MeetingCreateRequest request
    ) {
        MeetingCreateResponse response = meetingService.createMeeting(user, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(MEETING_CREATE_SUCCESS, response));
    }
}
