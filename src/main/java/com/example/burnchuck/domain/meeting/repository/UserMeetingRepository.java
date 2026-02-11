package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserMeetingRepository extends JpaRepository<UserMeeting, Long>, UserMeetingCustomRepository {

    boolean existsByUserIdAndMeetingId(Long userId, Long meetingId);

    Optional<UserMeeting> findByUserIdAndMeetingId(Long userId, Long meetingId);

    @Query("""
        SELECT um
        FROM UserMeeting um
        WHERE um.meeting = :meeting AND um.meetingRole = 'host'
        """)
    UserMeeting findHostByMeeting(@Param("meeting") Meeting meeting);

    int countByMeeting(Meeting meeting);

    @Query("""
        SELECT um.meeting.id as meetingId, count(um) as attendees
        FROM UserMeeting um
        JOIN um.meeting
        WHERE um.meeting.id in :meetingIdList
        GROUP BY um.meeting.id
        """)
    List<Object[]> countAllByMeetingIdIn(@Param("meetingIdList") Set<Long> meetingIdList);

    default UserMeeting findUserMeeting(Long userId, Long meetingId) {
        return findByUserIdAndMeetingId(userId, meetingId)
            .orElseThrow(() -> new CustomException(ErrorCode.ATTENDANCE_NOT_FOUND));
    }
}
