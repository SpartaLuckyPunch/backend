package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingDetailResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMapPointResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingCustomRepository {

    Page<MeetingSummaryResponse> findMeetingList(MeetingSearchRequest searchRequest, List<Long> meetingIdList, Pageable pageable);

    Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId);

    Page<MeetingSummaryWithStatusResponse> findHostedMeetings(Long userId, Pageable pageable);

    List<Meeting> findActiveHostedMeetings(Long userId);

    List<Meeting> findActivateMeetingsForNotification(LocalDateTime startDate, LocalDateTime endDate);

    List<MeetingMapPointResponse> findMeetingPointList(List<Long> meetingIdList);
}
