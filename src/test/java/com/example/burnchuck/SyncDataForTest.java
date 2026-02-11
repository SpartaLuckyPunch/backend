package com.example.burnchuck;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.service.ElasticsearchService;
import com.example.burnchuck.domain.meeting.service.MeetingCacheService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SyncDataForTest {

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private MeetingCacheService meetingCacheService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    @Test
    public void syncRedis() {

        List<Meeting> meetingList = meetingRepository.findActivateMeetingsForSchedules();

        for (Meeting meeting : meetingList) {
            meetingCacheService.saveMeetingLocation(meeting);
        }
    }

    @Test
    public void syncElasticSearch() {

        List<Meeting> meetingList = meetingRepository.findActivateMeetingsForSchedules();

        for (Meeting meeting : meetingList) {
            elasticsearchService.saveMeeting(meeting);
        }
    }
}
