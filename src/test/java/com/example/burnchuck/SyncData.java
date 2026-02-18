package com.example.burnchuck;

import com.example.burnchuck.common.document.MeetingDocument;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.service.MeetingCacheTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@SpringBootTest
public class SyncData {

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private MeetingCacheTest meetingCacheTest;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private static final int BATCH_SIZE = 10000;

    @Test
    @DisplayName("ES 동기화")
    void syncES() {

        long lastId = 0L;

        while (true) {

            List<Meeting> meetingList = meetingRepository.findMeetingListForSync(lastId, PageRequest.of(0, BATCH_SIZE));

            if (meetingList.isEmpty()) {
                break;
            }

            List<MeetingDocument> documentList = meetingList.stream()
                .map(meeting -> new MeetingDocument(meeting, 0, 0))
                .toList();

            elasticsearchOperations.save(documentList);

            lastId = meetingList.get(meetingList.size() -1).getId();
        }
    }

    @Test
    @DisplayName("Redis GEO 동기화")
    void syncRedis() {

        long lastId = 0L;

        while (true) {

            List<Meeting> meetingList = meetingRepository.findMeetingListForSync(lastId, PageRequest.of(0, BATCH_SIZE));

            if (meetingList.isEmpty()) {
                break;
            }

            for (Meeting meeting : meetingList) {
                meetingCacheTest.saveMeetingLocation(meeting);
            }

            lastId = meetingList.get(meetingList.size() -1).getId();
        }
    }
}
