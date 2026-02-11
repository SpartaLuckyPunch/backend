package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingCustomRepository {

    Optional<Meeting> findByIdAndIsDeletedFalse(Long id);

    @Query("""
            SELECT m
            FROM Meeting m
            JOIN FETCH m.category
            WHERE m.status != com.example.burnchuck.common.enums.MeetingStatus.COMPLETED AND m.isDeleted = false
            """)
    List<Meeting> findActivateMeetingsForSchedules();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Meeting m WHERE m.id = :id AND m.isDeleted = false")
    Optional<Meeting> findByIdWithLock(@Param("id") Long id);

    default Meeting findActivateMeetingById(Long id) {
        return findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }

    default Meeting findMeetingById(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }
}
