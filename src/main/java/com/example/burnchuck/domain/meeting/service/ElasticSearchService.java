package com.example.burnchuck.domain.meeting.service;

import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch._types.SortMode;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.TopRightBottomLeftGeoBounds;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingDocument;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.request.UserLocationRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMapPointResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.repository.MeetingDocumentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final MeetingDocumentRepository meetingDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void saveMeeting(Meeting meeting) {
        MeetingDocument meetingDocument = new MeetingDocument(meeting);
        meetingDocumentRepository.save(meetingDocument);
    }

    /**
     * 모임 목록 조회
     */
    public PageResponse<MeetingSummaryResponse> searchInListFormat(
        MeetingSearchRequest searchRequest,
        UserLocationRequest userLocationRequest,
        MeetingSortOption order,
        Pageable pageable
    ) {
        MeetingSortOption sort = order == null ? MeetingSortOption.LATEST : order;

        SortOptions sortOptions =
            switch (sort) {
                case NEAREST -> sortNEAREST(userLocationRequest);
                default -> sortLATEST();
            };

        NativeQuery query = NativeQuery.builder()
            .withQuery(build(searchRequest, null, userLocationRequest))
            .withSort(sortOptions)
            .withPageable(pageable)
            .build();

        SearchHits<MeetingDocument> search = elasticsearchOperations.search(query, MeetingDocument.class);
        SearchPage<MeetingDocument> searchPage = SearchHitSupport.searchPageFor(search, query.getPageable());

        List<MeetingSummaryResponse> content = searchPage.getSearchHits().stream()
            .map(SearchHit::getContent)
            .map(MeetingSummaryResponse::new)
            .collect(Collectors.toList());
        long totalHits = searchPage.getTotalElements();

        return new PageResponse<>(content, totalHits, searchPage.getTotalPages(), pageable.getPageSize(), pageable.getPageNumber());
    }

    /**
     * 모임 지도 조회
     */
    public List<MeetingMapPointResponse> searchInMapFormat(MeetingSearchRequest searchRequest, MeetingMapViewPortRequest viewPort) {

        NativeQuery query = NativeQuery.builder()
            .withQuery(build(searchRequest, viewPort, null))
            .build();

        SearchHits<MeetingDocument> search = elasticsearchOperations.search(query, MeetingDocument.class);

        return search.stream()
            .map(SearchHit::getContent)
            .map(MeetingMapPointResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 키워드 검색(제목), 카테고리, 일정 범위, 유저 위치 반경 범위
     */
    private Query build(MeetingSearchRequest searchRequest, MeetingMapViewPortRequest viewPort, UserLocationRequest userLocationRequest) {

        Query name = nameContains(searchRequest.getKeyword());

        List<Query> filters = new ArrayList<>();

        Query type = categoryEq(searchRequest.getCategory());
        if (type != null) filters.add(type);

        Query status = statusOpen();
        if (status != null) filters.add(status);

        Query radiusDistance = inDistance(userLocationRequest);
        if (radiusDistance != null) filters.add(radiusDistance);

        Query boundingBox = inBoundingBox(viewPort);
        if (boundingBox != null) filters.add(boundingBox);

        Query date = dateBetween(searchRequest.getStartDate(), searchRequest.getEndDate());
        if (date != null) filters.add(date);

        Query time = timeBetween(searchRequest.getStartTime(), searchRequest.getEndTime());
        if (time != null) filters.add(time);

        return Query.of(q -> q.bool(b -> {
            if (name != null) {
                b.must(name);
            }
            if (!filters.isEmpty()) {
                b.filter(filters);
            }
            return b;
        }));
    }

    private Query nameContains(String keyword){
        return StringUtils.hasText(keyword)
            ? Query.of(q -> q.matchPhrase(m -> m.field("title").query(keyword)))
            : null;
    }

    private Query categoryEq(String categoryCode){
        return categoryCode != null
            ? Query.of(q->q.term(t->t.field("categoryCode").value(categoryCode)))
            : null;
    }

    private Query statusOpen(){
        return Query.of(q->q.term(t->t.field("status").value(MeetingStatus.OPEN.toString())));
    }

    private Query inDistance(UserLocationRequest userLocationRequest) {

        if (userLocationRequest == null) {
            return null;
        }

        double finalDistance = userLocationRequest.getDistance() == null ? 5.0 : userLocationRequest.getDistance();

        return Query.of(q -> q.geoDistance(g -> g.field("geoPoint")
            .distance(finalDistance + "km")
            .location(gl -> gl.latlon(ll -> ll
                .lat(userLocationRequest.getLatitude())
                .lon(userLocationRequest.getLongitude()))
            )));
    }

    private Query inBoundingBox(MeetingMapViewPortRequest viewPort) {

        if (viewPort == null || !viewPort.notNull()) {
            return null;
        }

        TopRightBottomLeftGeoBounds bounds =
            TopRightBottomLeftGeoBounds.of(b -> b
                .topRight(tr -> tr.latlon(ll -> ll
                    .lat(viewPort.getMaxLat())
                    .lon(viewPort.getMaxLng())
                ))
                .bottomLeft(bl -> bl.latlon(ll -> ll
                    .lat(viewPort.getMinLat())
                    .lon(viewPort.getMinLng())
                ))
            );

        return Query.of(q -> q.geoBoundingBox(g -> g
            .field("geoPoint")
            .boundingBox(bb -> bb.trbl(bounds))
        ));
    }

    private Query dateBetween(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null
            ? Query.of(q -> q.range(r -> r
            .date(d -> d.field("meetingDatetime")
                .gte(startDate.toString())
                .lte(endDate.toString())
            )))
            : null;
    }

    private Query timeBetween(Integer startTime, Integer endTime) {
        return startTime != null && endTime != null
            ? Query.of(q -> q.range(r -> r
            .date(d -> d.field("meetingTime")
                .gte(startTime.toString())
                .lt(endTime.toString())
            )))
            : null;
    }

    private SortOptions sortLATEST() {
        return new SortOptions.Builder()
            .field(f -> f.field("createdDatetime").order(SortOrder.Desc)).build();
    }

    private SortOptions sortNEAREST(UserLocationRequest userLocationRequest) {

        return new SortOptions.Builder()
            .geoDistance(gd -> gd
                .field("geoPoint")
                .location(gl -> gl.latlon(ll -> ll
                    .lat(userLocationRequest.getLatitude())
                    .lon(userLocationRequest.getLongitude())))
                .order(SortOrder.Asc)
                .unit(DistanceUnit.Kilometers)
                .mode(SortMode.Min)
            )
            .build();
    }
}
