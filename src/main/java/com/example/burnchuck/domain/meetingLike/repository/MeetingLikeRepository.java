package com.example.burnchuck.domain.meetingLike.repository;

import static com.example.burnchuck.common.enums.ErrorCode.MEETING_LIKE_NOT_FOUND;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingLike;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.exception.CustomException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MeetingLikeRepository extends JpaRepository<MeetingLike, Long> {

    void deleteByUserId(Long id);

    boolean existsByUserAndMeeting(User user, Meeting meeting);

    Optional<MeetingLike> findByUserAndMeeting(User user, Meeting meeting);

    long countByMeeting(Meeting meeting);

    @Query("""
        SELECT ml.meeting.id, COUNT(ml)
        FROM MeetingLike ml
        WHERE ml.meeting.id IN :meetingIds
        GROUP BY ml.meeting.id
    """)
    List<Object[]> findByMeetingIdIn(@Param("meetingIds") Set<Long> meetingIds);

    default MeetingLike findByUserAndMeetingOrThrow(User user, Meeting meeting) {
        return findByUserAndMeeting(user, meeting)
            .orElseThrow(() -> new CustomException(MEETING_LIKE_NOT_FOUND));
    }
}
