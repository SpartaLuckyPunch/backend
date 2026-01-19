package com.example.burnchuck.domain.attendance.repository;

import com.example.burnchuck.common.entity.UserMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMeetingRepository extends JpaRepository<UserMeeting, Long> {

    boolean existsByUserIdAndMeetingId(Long userId, Long meetingId);
}
