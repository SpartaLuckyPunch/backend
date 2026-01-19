package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingCustomRepository {

    Optional<Meeting> findByIdAndIsDeletedFalse(Long id);

    default Meeting findActivateMeetingById(Long id) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }

}
