package com.example.burnchuck.domain.meeting.repository;

import static com.example.burnchuck.common.entity.QCategory.category1;
import static com.example.burnchuck.common.entity.QMeeting.meeting;
import static com.example.burnchuck.common.entity.QMeetingLike.meetingLike;
import static com.example.burnchuck.common.entity.QNotification.notification;
import static com.example.burnchuck.common.entity.QUserMeeting.userMeeting;

import com.example.burnchuck.common.dto.BoundingBox;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapSearchRequest;
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
            Pageable pageable,
            BoundingBox boundingBox,
            List<Long> meetingIdList
    ) {
        MeetingSortOption sort = request.getOrder() == null ? MeetingSortOption.LATEST : request.getOrder();

        if (request.getStartDatetime() != null || request.getEndDatetime() != null) {
            sort = MeetingSortOption.UPCOMING;
        }

        OrderSpecifier<?> orderSpecifier =
            switch (sort) {
                case POPULAR -> meetingLike.id.countDistinct().desc();
                case NEAREST -> orderByListOrder(meetingIdList);
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
                    keywordContains(request.getKeyword()),
                    categoryEq(request.getCategory()),
                    startAt(request.getStartDatetime()),
                    endAt(request.getEndDatetime()),
                    locationInBoundingBox(boundingBox),
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
                keywordContains(request.getKeyword()),
                categoryEq(request.getCategory()),
                startAt(request.getStartDatetime()),
                endAt(request.getEndDatetime()),
                locationInBoundingBox(boundingBox),
                inMeetingIdList(meetingIdList)
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 모임 지도 조회
     */
    @Override
    public List<MeetingMapPointResponse> findMeetingPointList(
        MeetingMapSearchRequest request,
        BoundingBox boundingBox
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
            .where(
                meeting.status.eq(MeetingStatus.OPEN),
                keywordContains(request.getKeyword()),
                categoryEq(request.getCategory()),
                startAt(request.getStartDatetime()),
                endAt(request.getEndDatetime()),
                locationInBoundingBox(boundingBox)
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
                        meeting.isDeleted.isFalse(),
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
                        meeting.isDeleted.isFalse(),
                        userMeeting.user.id.eq(userId),
                        userMeeting.meetingRole.eq(MeetingRole.HOST)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
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
                meeting.isDeleted.isFalse(),
                notification.id.isNull(),
                meeting.meetingDateTime.between(startDate, endDate)
            )
            .fetch();
    }

    private BooleanExpression categoryEq(String categoryName) {

        if (categoryName == null) {
            return null;
        }

        return category1.category.eq(categoryName);
    }

    private BooleanExpression inMeetingIdList(List<Long> meetingIdList) {

        if (meetingIdList == null) {
            return null;
        }

        return meeting.id.in(meetingIdList);
    }

    // 키워드 검색
    private BooleanExpression keywordContains(String keyword) {
        return (keyword != null && !keyword.isBlank())
                ? meeting.title.containsIgnoreCase(keyword) : null;
    }

    // 모임 시간 시작 범위
    private BooleanExpression startAt(LocalDateTime startDatetime) {
        return startDatetime != null ? meeting.meetingDateTime.after(startDatetime) : null;
    }

    // 모임 시간 끝 범위
    private BooleanExpression endAt(LocalDateTime endDatetime) {
        return endDatetime != null ? meeting.meetingDateTime.before(endDatetime) : null;
    }

    /**
     * BoundingBox 이내의 모임인지 확인
     */
    private BooleanExpression locationInBoundingBox(BoundingBox boundingBox) {

        if (boundingBox == null) {
            return null;
        }

        String lineString = String.format(
            "LINESTRING(%f %f, %f %f)",
            boundingBox.getMinLat(), boundingBox.getMinLng(),
            boundingBox.getMaxLat(), boundingBox.getMaxLng()
        );

        return Expressions.numberTemplate(
            Integer.class,
            "MBRContains(ST_LINESTRINGFROMTEXT({0}, 4326), {1})",
            lineString,
            meeting.point
        ).eq(1);
    }

    /**
     * 주어진 리스트 순서대로 정렬
     */
    private OrderSpecifier<Integer> orderByListOrder(List<Long> meetingIdList) {

        if (meetingIdList == null || meetingIdList.isEmpty()) {
            return null;
        }

        String ids = meetingIdList.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));

        NumberTemplate<Integer> orderExpr = Expressions.numberTemplate(
            Integer.class,
            "FIELD({0}, " + ids + ")",
            meeting.id
        );

        return orderExpr.asc();
    }
}