package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetingCustomRepository {

    Page<MeetingSummaryResponse> findMeetingList(
            String category,
            Pageable pageable
    );

    Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId);

    Page<MeetingSummaryWithStatusResponse> findHostedMeetings(
            Long userId,
            Pageable pageable
    );

    Page<MeetingSummaryResponse> searchMeetings(MeetingSearchRequest request, Pageable pageable);
}
