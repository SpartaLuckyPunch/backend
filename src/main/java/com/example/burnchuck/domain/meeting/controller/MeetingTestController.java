package com.example.burnchuck.domain.meeting.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.MEETING_GET_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.request.UserLocationRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMapPointResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.service.MeetingTestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class MeetingTestController {

    private final MeetingTestService meetingTestService;

    @GetMapping("/db/list")
    public ResponseEntity<CommonResponse<PageResponse<MeetingSummaryResponse>>> dbSearchList(
        @ModelAttribute MeetingSearchRequest searchRequest,
        @ModelAttribute UserLocationRequest userLocationRequest,
        @RequestParam(required = false) MeetingSortOption order,
        @PageableDefault(size = 6) Pageable pageable
    ) {
        Page<MeetingSummaryResponse> response = meetingTestService.dbSearchList(searchRequest, userLocationRequest, order, pageable);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(MEETING_GET_SUCCESS, PageResponse.from(response)));
    }

    @GetMapping("/db/map")
    public ResponseEntity<CommonResponse<List<MeetingMapPointResponse>>> dbSearchMap(
        @ModelAttribute MeetingSearchRequest searchRequest,
        @ModelAttribute MeetingMapViewPortRequest viewPort
    ) {
        List<MeetingMapPointResponse> response = meetingTestService.dbSearchMap(searchRequest, viewPort);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(MEETING_GET_SUCCESS, response));
    }

    @GetMapping("/redis/list")
    public ResponseEntity<CommonResponse<PageResponse<MeetingSummaryResponse>>> redisSearchList(
        @ModelAttribute MeetingSearchRequest searchRequest,
        @ModelAttribute UserLocationRequest userLocationRequest,
        @RequestParam(required = false) MeetingSortOption order,
        @PageableDefault(size = 6) Pageable pageable
    ) {
        Page<MeetingSummaryResponse> response = meetingTestService.redisSearchList(searchRequest, userLocationRequest, order, pageable);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(MEETING_GET_SUCCESS, PageResponse.from(response)));
    }

    @GetMapping("/redis/map")
    public ResponseEntity<CommonResponse<List<MeetingMapPointResponse>>> redisSearchMap(
        @ModelAttribute MeetingSearchRequest searchRequest,
        @ModelAttribute MeetingMapViewPortRequest viewPort
    ) {
        List<MeetingMapPointResponse> response = meetingTestService.redisSearchMap(searchRequest, viewPort);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(MEETING_GET_SUCCESS, response));
    }

    @GetMapping("/es/list")
    public ResponseEntity<CommonResponse<PageResponse<MeetingSummaryResponse>>> esSearchList(
        @ModelAttribute MeetingSearchRequest searchRequest,
        @ModelAttribute UserLocationRequest userLocationRequest,
        @RequestParam(required = false) MeetingSortOption order,
        @PageableDefault(size = 6) Pageable pageable
    ) {
        PageResponse<MeetingSummaryResponse> response = meetingTestService.esSearchList(searchRequest, userLocationRequest, order, pageable);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(MEETING_GET_SUCCESS, response));
    }

    @GetMapping("/es/map")
    public ResponseEntity<CommonResponse<List<MeetingMapPointResponse>>> esSearchMap(
        @ModelAttribute MeetingSearchRequest searchRequest,
        @ModelAttribute MeetingMapViewPortRequest viewPort
    ) {
        List<MeetingMapPointResponse> response = meetingTestService.esSearchMap(searchRequest, viewPort);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(MEETING_GET_SUCCESS, response));
    }
}
