package com.example.burnchuck.domain.meeting.repository;

import static com.example.burnchuck.common.entity.QCategory.category1;
import static com.example.burnchuck.common.entity.QMeeting.meeting;
import static com.example.burnchuck.common.entity.QMeetingLike.meetingLike;
import static com.example.burnchuck.common.entity.QUserMeeting.userMeeting;

import com.example.burnchuck.common.dto.CustomBoundingBox;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMapPointResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingSearchTempRepository {

    private final JPAQueryFactory queryFactory;

    public Page<MeetingSummaryResponse> findMeetingList(
        MeetingSearchRequest request,
        MeetingSortOption order,
        Pageable pageable,
        CustomBoundingBox boundingBox,
        List<Long> meetingIdList
    ) {
        MeetingSortOption sort = order == null ? MeetingSortOption.LATEST : order;

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
                betweenDate(request.getStartDate(), request.getEndDate()),
                betweenTime(request.getStartTime(), request.getEndTime()),
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
                betweenDate(request.getStartDate(), request.getEndDate()),
                betweenTime(request.getStartTime(), request.getEndTime()),
                locationInBoundingBox(boundingBox),
                inMeetingIdList(meetingIdList)
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 모임 지도 조회
     */
    public List<MeetingMapPointResponse> findMeetingPointList(
        MeetingSearchRequest request,
        CustomBoundingBox boundingBox,
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
            .where(
                meeting.status.eq(MeetingStatus.OPEN),
                keywordContains(request.getKeyword()),
                categoryEq(request.getCategory()),
                betweenDate(request.getStartDate(), request.getEndDate()),
                betweenTime(request.getStartTime(), request.getEndTime()),
                locationInBoundingBox(boundingBox),
                inMeetingIdList(meetingIdList)
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

    // 모임 날짜 범위
    private BooleanExpression betweenDate(LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null) {
            return null;
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return meeting.meetingDateTime.between(startDateTime, endDateTime);
    }

    // 모임 시간 범위
    private BooleanExpression betweenTime(Integer startTime, Integer endTime) {

        if (startTime == null || endTime == null) {
            return null;
        }

        NumberTemplate<Integer> timeTemplate = Expressions.numberTemplate(
            Integer.class,
            "HOUR({0})",
            meeting.meetingDateTime
        );

        return timeTemplate.between(startTime, endTime);
    }

    /**
     * BoundingBox 이내의 모임인지 확인
     */
    private BooleanExpression locationInBoundingBox(CustomBoundingBox boundingBox) {

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
