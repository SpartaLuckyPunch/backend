package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import com.example.burnchuck.domain.meeting.model.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.model.response.HostedMeetingResponse;
import com.example.burnchuck.domain.meeting.model.response.MeetingDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MeetingCustomRepository {

    Page<MeetingSummaryDto> findMeetingList(
            String category,
            Pageable pageable
    );

    Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId);

    Page<HostedMeetingResponse> findHostedMeetings(
            Long userId,
            Pageable pageable
    );

    Page<MeetingSummaryDto> searchMeetings(MeetingSearchRequest request, Pageable pageable);
}
