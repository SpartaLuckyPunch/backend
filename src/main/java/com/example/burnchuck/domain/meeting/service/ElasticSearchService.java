package com.example.burnchuck.domain.meeting.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingDocument;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.repository.MeetingDocumentRepository;
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

    public List<MeetingDocument> searchByName(MeetingSearchRequest searchRequest) {

        NativeQuery query = NativeQuery.builder()
            .withQuery(buildSearchQuery(searchRequest))
            .build();

        SearchHits<MeetingDocument> search = elasticsearchOperations.search(query, MeetingDocument.class);

        return search.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }

    private Query buildSearchQuery(MeetingSearchRequest searchRequest) {
        return Query.of(q->q.bool(b-> {

            Query name = nameContains(searchRequest.getKeyword());
            if (name != null) b.must(name);

            Query type = categoryEq(searchRequest.getCategory());
            if (type != null) b.filter(type);

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
}
