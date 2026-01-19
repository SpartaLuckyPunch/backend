package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    default Meeting findMeetingById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));

    }
}
