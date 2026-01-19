package com.example.burnchuck.domain.attendance.repository;

import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMeetingRepository extends JpaRepository<UserMeeting, Long> {

    boolean existsByUserIdAndMeetingId(Long userId, Long meetingId);

    Optional<UserMeeting> findByUserIdAndMeetingId(Long userId, Long meetingId);

    default UserMeeting findUserMeeting(Long userId, Long meetingId) {
        return findByUserIdAndMeetingId(userId, meetingId)
            .orElseThrow(() -> new CustomException(ErrorCode.ATTENDANCE_NOT_FOUND));
    }
}
