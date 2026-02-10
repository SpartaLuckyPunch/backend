package com.example.burnchuck.domain.meeting.repository;

import static com.example.burnchuck.common.entity.QCategory.category1;
import static com.example.burnchuck.common.entity.QMeeting.meeting;
import static com.example.burnchuck.common.entity.QMeetingLike.meetingLike;
import static com.example.burnchuck.common.entity.QNotification.notification;
import static com.example.burnchuck.common.entity.QUserMeeting.userMeeting;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingDetailResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMapPointResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class MeetingCustomRepositoryImpl implements MeetingCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 모임 전체 조회
     */
    @Override
    public Page<MeetingSummaryResponse> findMeetingList(
            MeetingSearchRequest request,
            List<Long> meetingIdList,
            Pageable pageable
    ) {
        MeetingSortOption sort = request.getOrder() == null ? MeetingSortOption.LATEST : request.getOrder();

        // TODO : 이렇게 하면 일정 범위 검색할 때 어떤 정렬을 사용하고 싶어도 사용할 수 없음
        if (request.getStartDate() != null || request.getEndDate() != null) {
            sort = MeetingSortOption.UPCOMING;
        }

        OrderSpecifier<?> orderSpecifier =
            switch (sort) {
                case POPULAR -> meetingLike.id.countDistinct().desc();
                case NEAREST -> meeting.id.asc();
                case UPCOMING -> meeting.meetingDateTime.asc();
                case LATEST -> meeting.createdDatetime.desc();
            };

        List<MeetingSummaryResponse> content = queryFactory
            .select(Projections.constructor(
                    MeetingSummaryResponse.class,
                    meeting.id,
                    meeting.title,
                    meeting.imgUrl,
                    meeting.location,
                    meeting.latitude,
                    meeting.longitude,
                    meeting.meetingDateTime,
                    meeting.maxAttendees,
                    userMeeting.id.countDistinct().intValue()
            ))
            .from(meeting)
            .leftJoin(userMeeting)
            .on(userMeeting.meeting.eq(meeting))
            .leftJoin(meeting.category, category1)
            .where(
                    meeting.status.eq(MeetingStatus.OPEN),
                    inMeetingIdList(meetingIdList)
            )
            .groupBy(meeting.id)
            .orderBy(orderSpecifier)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(meeting.count())
            .from(meeting)
            .leftJoin(meeting.category, category1)
            .where(
                meeting.status.eq(MeetingStatus.OPEN),
                inMeetingIdList(meetingIdList)
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 모임 지도 조회
     */
    @Override
    public List<MeetingMapPointResponse> findMeetingPointList(
        List<Long> meetingIdList
    ) {
        return queryFactory
            .select(Projections.constructor(
                MeetingMapPointResponse.class,
                meeting.id,
                meeting.title,
                meeting.latitude,
                meeting.longitude
            ))
            .from(meeting)
            .leftJoin(meeting.category, category1)
            .where(
                meeting.status.eq(MeetingStatus.OPEN),
                inMeetingIdList(meetingIdList)
            )
            .fetch();
    }

    /**
     * 모임 상세 조회
     */
    @Override
    public Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId) {

        MeetingDetailResponse result = queryFactory
                .select(Projections.constructor(
                        MeetingDetailResponse.class,
                        meeting.id,
                        meeting.title,
                        meeting.imgUrl,
                        meeting.description,
                        meeting.location,
                        meeting.latitude,
                        meeting.longitude,
                        meeting.meetingDateTime,
                        meeting.maxAttendees,
                        userMeeting.id.countDistinct().intValue(),
                        meeting.status.stringValue(),
                        meetingLike.id.countDistinct(),
                        meeting.views
                ))
                .from(meeting)
                .leftJoin(userMeeting)
                .on(userMeeting.meeting.eq(meeting))
                .leftJoin(meetingLike)
                .on(meetingLike.meeting.eq(meeting))
                .where(meeting.id.eq(meetingId))
                .groupBy(meeting.id)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 내가 주최한 모임 조회
     */
    public Page<MeetingSummaryWithStatusResponse> findHostedMeetings(
            Long userId,
            Pageable pageable
    ) {

        List<MeetingSummaryWithStatusResponse> content = queryFactory
                .select(Projections.constructor(
                        MeetingSummaryWithStatusResponse.class,
                        meeting.id,
                        meeting.title,
                        meeting.imgUrl,
                        meeting.location,
                        meeting.meetingDateTime,
                        meeting.status.stringValue(),
                        meeting.maxAttendees,
                        userMeeting.id.countDistinct()
                ))
                .from(userMeeting)
                .join(userMeeting.meeting, meeting)
                .where(
                        userMeeting.user.id.eq(userId),
                        userMeeting.meetingRole.eq(MeetingRole.HOST)
                )
                .groupBy(meeting.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(meeting.id.countDistinct())
                .from(userMeeting)
                .join(userMeeting.meeting, meeting)
                .where(
                        userMeeting.user.id.eq(userId),
                        userMeeting.meetingRole.eq(MeetingRole.HOST)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    /**
     * 주최한 모임 중 COMPLETED 되지 않은 모임 조회
     */
    @Override
    public List<Meeting> findActiveHostedMeetings(Long userId) {
        return queryFactory
            .select(userMeeting.meeting)
            .from(userMeeting)
            .join(userMeeting.meeting, meeting)
            .where(
                userMeeting.user.id.eq(userId),
                userMeeting.meetingRole.eq(MeetingRole.HOST),
                meeting.status.ne(MeetingStatus.COMPLETED)
            )
            .fetch();
    }

    /**
     * TaskSchedule 복구 대상 모임 조회
     */
    @Override
    public List<Meeting> findActivateMeetingsForNotification(LocalDateTime startDate, LocalDateTime endDate) {

        return queryFactory
            .selectFrom(meeting)
            .leftJoin(notification).on(
                notification.meeting.id.eq(meeting.id),
                notification.type.eq(NotificationType.COMMENT_REQUESTED)
            )
            .where(
                meeting.status.eq(MeetingStatus.COMPLETED),
                notification.id.isNull(),
                meeting.meetingDateTime.between(startDate, endDate)
            )
            .fetch();
    }

    private BooleanExpression inMeetingIdList(List<Long> meetingIdList) {

        if (meetingIdList == null) {
            return null;
        }

        return meeting.id.in(meetingIdList);
    }

    /**
     * 주어진 리스트 순서대로 정렬
     */
    private OrderSpecifier<Long> orderByListOrder(List<Long> meetingIdList) {

        if (meetingIdList == null || meetingIdList.isEmpty()) {
            return meeting.id.asc();
        }

        String ids = meetingIdList.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));

        NumberTemplate<Long> orderExpr = Expressions.numberTemplate(
            Long.class,
            "FIELD({0}, " + ids + ")",
            meeting.id
        );

        return orderExpr.asc();
    }
}