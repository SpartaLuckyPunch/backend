package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.exception.CustomException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingCustomRepository {

    Optional<Meeting> findByIdAndIsDeletedFalse(Long id);

    @Query("""
            SELECT m
            FROM Meeting m
            WHERE m.status != :status AND m.isDeleted = false
            """)
    List<Meeting> findActivateMeetingByStatusNot(@Param("status") MeetingStatus meetingStatus);

    default Meeting findActivateMeetingById(Long id) {
        return findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }
}
