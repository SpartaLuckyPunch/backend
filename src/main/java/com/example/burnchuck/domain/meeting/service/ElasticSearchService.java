package com.example.burnchuck.domain.meeting.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.TopRightBottomLeftGeoBounds;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingDocument;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapSearchRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
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
import org.springframework.data.elasticsearch.core.SearchHits;
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

    // TODO: 메서드 합치기

    /**
     * 모임 목록 조회
     */
    public List<Long> searchInListFormat(MeetingSearchRequest searchRequest, Location location, Pageable pageable) {

        MeetingSortOption sort = searchRequest.getOrder() == null ? MeetingSortOption.LATEST : searchRequest.getOrder();

        SortOptions sortOptions =
            switch (sort) {
                default -> sortLATEST();
            };

        NativeQuery query = NativeQuery.builder()
            .withQuery(buildSearchQueryForListSearch(searchRequest, location))
            .withSort(sortOptions)
            .withPageable(pageable)
            .build();

        SearchHits<MeetingDocument> search = elasticsearchOperations.search(query, MeetingDocument.class);

        return search.stream()
            .map(SearchHit::getContent)
            .map(meetingDocument -> Long.parseLong(meetingDocument.getId()))
            .collect(Collectors.toList());
    }

    private SortOptions sortLATEST() {
        return new SortOptions.Builder()
            .field(f -> f.field("createdDatetime").order(SortOrder.Desc)).build();
    }

    /**
     * 키워드 검색(제목), 카테고리, 일정 범위, 유저 위치 반경 범위
     */
    private Query buildSearchQueryForListSearch(MeetingSearchRequest searchRequest, Location location) {

        Query name = nameContains(searchRequest.getKeyword());

        List<Query> filters = new ArrayList<>();

        Query type = categoryEq(searchRequest.getCategory());
        if (type != null) filters.add(type);

        Query radiusDistance = inDistance(searchRequest.getDistance(), location);
        if (radiusDistance != null) filters.add(radiusDistance);

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

    /**
     * 모임 지도 조회
     */
    public List<Long> searchInMapFormat(MeetingMapSearchRequest searchRequest, MeetingMapViewPortRequest viewPort) {

        NativeQuery query = NativeQuery.builder()
            .withQuery(buildSearchQueryForListSearch(searchRequest, viewPort))
            .build();

        SearchHits<MeetingDocument> search = elasticsearchOperations.search(query, MeetingDocument.class);

        return search.stream()
            .map(SearchHit::getContent)
            .map(meetingDocument -> Long.parseLong(meetingDocument.getId()))
            .collect(Collectors.toList());
    }

    /**
     * 키워드 검색(제목), 카테고리, 일정 범위, 지도 Viewport
     */
    private Query buildSearchQueryForListSearch(MeetingMapSearchRequest searchRequest, MeetingMapViewPortRequest viewPort) {

        Query name = nameContains(searchRequest.getKeyword());

        List<Query> filters = new ArrayList<>();

        Query type = categoryEq(searchRequest.getCategory());
        if (type != null) filters.add(type);

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

    private Query inDistance(Double distance, Location location) {

        double finalDistance = distance == null ? 5.0 : distance;

        return location != null
            ? Query.of(q -> q.geoDistance(g -> g.field("geoPoint")
            .distance(finalDistance + "km")
            .location(gl -> gl.latlon(ll -> ll
                .lat(location.getLatitude())
                .lon(location.getLongitude()))
            )))
            :null;
    }

    private Query inBoundingBox(MeetingMapViewPortRequest viewPort) {

        if (!viewPort.notNull()) {
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
}
