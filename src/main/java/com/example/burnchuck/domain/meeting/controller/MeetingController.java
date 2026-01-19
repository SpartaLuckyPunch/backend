package com.example.burnchuck.domain.meeting.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import com.example.burnchuck.domain.meeting.model.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.model.response.MeetingCreateResponse;
import com.example.burnchuck.domain.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_CREATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_GET_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    /**
     * 모임 생성
     */
    @PostMapping
    public ResponseEntity<CommonResponse<MeetingCreateResponse>> createMeeting(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody MeetingCreateRequest request
    ) {
        MeetingCreateResponse response = meetingService.createMeeting(user, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(MEETING_CREATE_SUCCESS, response));
    }

    /**
     * 모임 전체 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<MeetingSummaryDto>>> getMeetings(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 6) Pageable pageable
    ) {
        Page<MeetingSummaryDto> page = meetingService.getMeetingPage(category, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(MEETING_GET_SUCCESS, PageResponse.from(page)));
    }
}
