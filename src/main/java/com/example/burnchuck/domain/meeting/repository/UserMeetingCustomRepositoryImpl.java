package com.example.burnchuck.domain.meeting.repository;

import static com.example.burnchuck.common.entity.QMeeting.meeting;
import static com.example.burnchuck.common.entity.QUserMeeting.userMeeting;

import com.example.burnchuck.common.entity.QUser;
import com.example.burnchuck.common.entity.QUserMeeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.domain.meeting.model.response.MeetingSummaryWithStatusResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserMeetingCustomRepositoryImpl implements UserMeetingCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MeetingSummaryWithStatusResponse> findAllMeetingsByUser(User user) {

        // meeting별 참석 인원 수를 카운트하기 위해 같은 테이블 join
        QUserMeeting attendee = new QUserMeeting("attendee");

        return queryFactory
            .select(Projections.constructor(
                MeetingSummaryWithStatusResponse.class,
                meeting.id,
                meeting.title,
                meeting.imgUrl,
                meeting.location,
                meeting.meetingDateTime,
                meeting.status.stringValue(),
                meeting.maxAttendees,
                attendee.id.count()
            ))
            .from(userMeeting)
            .join(userMeeting.meeting, meeting)
            .join(attendee).on(attendee.meeting.eq(meeting))
            .where(userMeeting.user.eq(user))
            .where(meeting.isDeleted.eq(false))
            .groupBy(meeting.id)
            .fetch();
    }


    @Override
    public List<UserMeeting> findMeetingMembers(Long meetingId) {

        QUserMeeting userMeeting = QUserMeeting.userMeeting;
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(userMeeting)
                .join(userMeeting.user, user).fetchJoin()
                .where(userMeeting.meeting.id.eq(meetingId))
                .fetch();
    }
}
