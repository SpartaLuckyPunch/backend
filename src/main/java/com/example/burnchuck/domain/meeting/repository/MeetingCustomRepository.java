package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingCustomRepository {

    Page<MeetingSummaryDto> findMeetingList(
            String category,
            Pageable pageable
    );
}
