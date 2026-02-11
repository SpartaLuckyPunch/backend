package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.MeetingDocument;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.domain.meeting.repository.MeetingDocumentRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final MeetingDocumentRepository meetingDocumentRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void saveMeeting(Meeting meeting) {
        MeetingDocument meetingDocument = new MeetingDocument(meeting, 0);
        meetingDocumentRepository.save(meetingDocument);
    }

    public void updateMeeting(Meeting meeting) {

        int currentAttendees = userMeetingRepository.countByMeeting(meeting);

        MeetingDocument meetingDocument = new MeetingDocument(meeting, currentAttendees);
        meetingDocumentRepository.save(meetingDocument);
    }

    public void deleteMeeting(Meeting meeting) {
        meetingDocumentRepository.deleteById(meeting.getId());
    }

    public void updateMeetingStatus(Long meetingId, MeetingStatus status) {

        Document document = Document.create();
        document.put("status", status.toString());

        UpdateQuery updateQuery = UpdateQuery.builder(meetingId.toString())
            .withDocument(document)
            .build();

        elasticsearchOperations.update(updateQuery, IndexCoordinates.of("meetings"));
    }
}
