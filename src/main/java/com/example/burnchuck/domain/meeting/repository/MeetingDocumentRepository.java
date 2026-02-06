package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.MeetingDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MeetingDocumentRepository extends ElasticsearchRepository<MeetingDocument, String> {

}
