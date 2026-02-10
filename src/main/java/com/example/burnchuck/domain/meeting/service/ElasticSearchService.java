package com.example.burnchuck.domain.meeting.service;

import co.elastic.clients.elasticsearch._types.TopRightBottomLeftGeoBounds;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingDocument;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.repository.MeetingDocumentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    public List<MeetingDocument> searchByName(MeetingSearchRequest searchRequest, MeetingMapViewPortRequest viewPort,Location location) {

        NativeQuery query = NativeQuery.builder()
            .withQuery(buildSearchQuery(searchRequest, viewPort,location))
            .build();

        SearchHits<MeetingDocument> search = elasticsearchOperations.search(query, MeetingDocument.class);

        return search.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }

    private Query buildSearchQuery(MeetingSearchRequest searchRequest, MeetingMapViewPortRequest viewPort,Location location) {

        Query name = nameContains(searchRequest.getKeyword());

        List<Query> filters = new ArrayList<>();

        Query type = categoryEq(searchRequest.getCategory());
        if (type != null) filters.add(type);

        Query radiusDistance = inDistance(searchRequest.getDistance(), location);
        if (radiusDistance != null) filters.add(radiusDistance);

        Query boundingBox = inBoundingBox(viewPort);
        if (boundingBox != null) filters.add(boundingBox);

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
        return location != null
            ? Query.of(q -> q.geoDistance(g -> g.field("geoPoint")
            .distance(distance + "km")
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
}
