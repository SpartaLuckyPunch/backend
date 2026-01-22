package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.QCategory;
import com.example.burnchuck.common.entity.QMeeting;
import com.example.burnchuck.common.entity.QMeetingLike;
import com.example.burnchuck.common.entity.QUserMeeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingDetailResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class MeetingCustomRepositoryImpl implements MeetingCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 모임 전체 조회
     */
    @Override
    public Page<MeetingSummaryResponse> findMeetingList(
            String category,
            Pageable pageable
    ) {

        QMeeting meeting = QMeeting.meeting;
        QUserMeeting userMeeting = QUserMeeting.userMeeting;
        QCategory category1 = QCategory.category1;

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
                        categoryEq(category),
                        meeting.status.eq(MeetingStatus.OPEN)
                )
                .groupBy(meeting.id)
                .orderBy(meeting.meetingDateTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory
                        .select(meeting.id.countDistinct())
                        .from(meeting)
                        .leftJoin(meeting.category, category1)
                        .where(
                                categoryEq(category),
                                meeting.status.eq(MeetingStatus.OPEN)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 모임 상세 조회
     */
    @Override
    public Optional<MeetingDetailResponse> findMeetingDetail(Long meetingId) {

        QMeeting meeting = QMeeting.meeting;
        QUserMeeting userMeeting = QUserMeeting.userMeeting;
        QMeetingLike meetingLike = QMeetingLike.meetingLike;

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
        QMeeting meeting = QMeeting.meeting;
        QUserMeeting userMeeting = QUserMeeting.userMeeting;

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
     * 모임 검색
     */
    @Override
    public Page<MeetingSummaryResponse> searchMeetings(MeetingSearchRequest request, Pageable pageable) {

        QMeeting meeting = QMeeting.meeting;
        QCategory qCategory = QCategory.category1;
        QUserMeeting userMeeting = QUserMeeting.userMeeting;
        QMeetingLike meetingLike = QMeetingLike.meetingLike;

        OrderSpecifier<?> orderSpecifier =
            switch (request.getOrder()) {

                case POPULAR -> meetingLike.id.countDistinct().desc();

                default -> meeting.createdDatetime.desc();
            };

        List<MeetingSummaryResponse> content = queryFactory
                .select(Projections.constructor(MeetingSummaryResponse.class,
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
                .leftJoin(meeting.category, qCategory)
                .leftJoin(userMeeting).on(userMeeting.meeting.eq(meeting))
                .leftJoin(meetingLike).on(meetingLike.meeting.eq(meeting))
                .where(
                        keywordContains(request.getKeyword()),
                        categoryEq(request.getCategory()),
                        meeting.status.eq(MeetingStatus.OPEN)
                )
                .groupBy(meeting.id)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(meeting.count())
                .from(meeting)
                .leftJoin(meeting.category, qCategory)
                .where(
                        keywordContains(request.getKeyword()),
                        categoryEq(request.getCategory()),
                        meeting.status.eq(MeetingStatus.OPEN)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression categoryEq(String categoryName) {

        if (categoryName == null) {
            return null;
        }

        return QCategory.category1.category.eq(categoryName);
    }

    // 키워드 검색
    private BooleanExpression keywordContains(String keyword) {
        return (keyword != null && !keyword.isBlank())
                ? QMeeting.meeting.title.containsIgnoreCase(keyword) : null;
    }
}