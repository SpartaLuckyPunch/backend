package com.example.burnchuck.domain.meeting.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_CREATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_DELETE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_GET_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_GET_HOSTED_LIST_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_SEARCH_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_UPDATE_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import com.example.burnchuck.domain.meeting.model.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.model.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.model.request.MeetingUpdateRequest;
import com.example.burnchuck.domain.meeting.model.response.HostedMeetingResponse;
import com.example.burnchuck.domain.meeting.model.response.MeetingCreateResponse;
import com.example.burnchuck.domain.meeting.model.response.MeetingDetailResponse;
import com.example.burnchuck.domain.meeting.model.response.MeetingUpdateResponse;
import com.example.burnchuck.domain.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MeetingController {

    private final MeetingService meetingService;

    /**
     * 모임 생성
     */
    @PostMapping("/meetings")
    public ResponseEntity<CommonResponse<MeetingCreateResponse>> createMeeting(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody MeetingCreateRequest request
    ) {
        MeetingCreateResponse response = meetingService.createMeetingAndNotify(user, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(MEETING_CREATE_SUCCESS, response));
    }

    /**
     * 모임 전체 조회
     */
    @GetMapping("/meetings")
    public ResponseEntity<CommonResponse<PageResponse<MeetingSummaryDto>>> getMeetings(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 6) Pageable pageable
    ) {
        Page<MeetingSummaryDto> page = meetingService.getMeetingPage(category, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(MEETING_GET_SUCCESS, PageResponse.from(page)));
    }

    /**
     * 모임 단건 조회
     */
    @GetMapping("/meetings/{meetingId}")
    public ResponseEntity<CommonResponse<MeetingDetailResponse>> getMeetingDetail(
            @PathVariable Long meetingId
    ) {
        MeetingDetailResponse response = meetingService.getMeetingDetail(meetingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(MEETING_GET_SUCCESS, response));
    }

    /**
     * 모임 삭제
     */
    @DeleteMapping("/meetings/{meetingId}")
    public ResponseEntity<CommonResponse<Void>> deleteMeeting(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long meetingId
    ) {
        meetingService.deleteMeeting(authUser, meetingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(MEETING_DELETE_SUCCESS));
    }

    /**
     * 모임 수정
     */
    @PatchMapping("/meetings/{meetingId}")
    public ResponseEntity<CommonResponse<MeetingUpdateResponse>> updateMeeting(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long meetingId,
            @Valid @RequestBody MeetingUpdateRequest request
    ) {
        MeetingUpdateResponse response = meetingService.updateMeeting(user, meetingId, request);

        return ResponseEntity.ok(
                CommonResponse.success(MEETING_UPDATE_SUCCESS, response)
        );
    }

    /**
     * 주최한 모임 목록 조회
     */
    @GetMapping("/me/meetings/hosted")
    public ResponseEntity<CommonResponse<PageResponse<HostedMeetingResponse>>> getHostedMeetings(
            @AuthenticationPrincipal AuthUser authUser,
            @PageableDefault(size = 6) Pageable pageable
    ) {
        Page<HostedMeetingResponse> page = meetingService.getHostedMeetings(authUser, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(MEETING_GET_HOSTED_LIST_SUCCESS, PageResponse.from(page)));
    }

    /**
     * 모임 검색
     */
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<PageResponse<MeetingSummaryDto>>> searchMeetings(
            @ModelAttribute MeetingSearchRequest searchRequest,
            @PageableDefault(size = 6) Pageable pageable
    ) {

        Page<MeetingSummaryDto> page = meetingService.searchMeetings(searchRequest, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(MEETING_SEARCH_SUCCESS, PageResponse.from(page)));
    }
}
