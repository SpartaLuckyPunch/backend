package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingCustomRepository {

    Optional<Meeting> findByIdAndIsDeletedFalse(Long id);

    @Query("""
            SELECT m
            FROM Meeting m
            WHERE m.status != com.example.burnchuck.common.enums.MeetingStatus.COMPLETED AND m.isDeleted = false
            """)
    List<Meeting> findActivateMeetingsForSchedules();

    default Meeting findActivateMeetingById(Long id) {
        return findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }
}
