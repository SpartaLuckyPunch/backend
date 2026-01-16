package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
}
