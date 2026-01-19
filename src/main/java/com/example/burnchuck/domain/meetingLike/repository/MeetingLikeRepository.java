package com.example.burnchuck.domain.meetingLike.repository;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingLike;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static com.example.burnchuck.common.enums.ErrorCode.MEETING_LIKE_NOT_FOUND;

public interface MeetingLikeRepository extends JpaRepository<MeetingLike, Long> {

    void deleteByUserId(Long id);

    boolean existsByUserAndMeeting(User user, Meeting meeting);

    Optional<MeetingLike> findByUserAndMeeting(User user, Meeting meeting);

    default MeetingLike findByUserAndMeetingOrThrow(User user, Meeting meeting) {
        return findByUserAndMeeting(user, meeting)
                .orElseThrow(() -> new CustomException(MEETING_LIKE_NOT_FOUND));
    }

    long countByMeeting(Meeting meeting);
}
