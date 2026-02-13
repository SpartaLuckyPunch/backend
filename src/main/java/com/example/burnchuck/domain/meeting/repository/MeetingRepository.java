package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingCustomRepository {

    Optional<Meeting> findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT m
            FROM Meeting m
            WHERE m.status != com.example.burnchuck.common.enums.MeetingStatus.COMPLETED AND m.deleted = false
            """)
    List<Meeting> findActivateMeetingsForSchedules();

    @Query("""
            SELECT m
            FROM Meeting m
            JOIN FETCH m.category
            WHERE m.status != com.example.burnchuck.common.enums.MeetingStatus.COMPLETED AND m.deleted = false
                AND m.id > :lastId
            """)
    List<Meeting> findMeetingListForSync(@Param("lastId") Long lastId, Pageable pageable);

    default Meeting findActivateMeetingById(Long id) {
        return findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }
}
