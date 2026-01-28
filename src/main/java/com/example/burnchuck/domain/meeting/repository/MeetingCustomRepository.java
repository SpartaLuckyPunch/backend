package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.dto.BoundingBox;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingDetailResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingCustomRepository {

    Page<MeetingSummaryResponse> findMeetingList(String category, Pageable pageable, BoundingBox boundingBox);

    Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId);

    Page<MeetingSummaryWithStatusResponse> findHostedMeetings(Long userId, Pageable pageable);

    Page<MeetingSummaryResponse> searchMeetings(
        MeetingSearchRequest request,
        BoundingBox boundingBox,
        Pageable pageable
    );

    List<Meeting> findActivateMeetingsForNotification(LocalDateTime startDate, LocalDateTime endDate);
}
