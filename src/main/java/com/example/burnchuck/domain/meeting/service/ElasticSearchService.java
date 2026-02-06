package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingDocument;
import com.example.burnchuck.domain.meeting.repository.MeetingDocumentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final MeetingDocumentRepository meetingDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    public void saveMeeting(Meeting meeting) {
        MeetingDocument meetingDocument = new MeetingDocument(meeting);
        meetingDocumentRepository.save(meetingDocument);
    }

    public List<MeetingDocument> searchByName(String keyword) {

        NativeQuery query = new NativeQueryBuilder()
            .withQuery(q -> q.multiMatch(m -> m
                .fields("title")
                .query(keyword)))
            .build();

        SearchHits<MeetingDocument> search = elasticsearchOperations.search(query, MeetingDocument.class);
        return search.stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }
}
