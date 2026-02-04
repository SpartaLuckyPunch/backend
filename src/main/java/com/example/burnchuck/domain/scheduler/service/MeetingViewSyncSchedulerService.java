package com.example.burnchuck.domain.scheduler.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.service.MeetingCacheService;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j(topic = "MeetingViewSyncScheduler")
public class MeetingViewSyncSchedulerService {

    private final MeetingRepository meetingRepository;
    private final MeetingCacheService meetingCacheService;

    @Scheduled(cron = "0 0 0 * * *")
    public void meetingViewSync() {

        LocalDate dayBeforeTody = LocalDate.now().minusDays(1);

        Set<TypedTuple<String>> allViewList = meetingCacheService.getAllViewList(dayBeforeTody);

        for (TypedTuple<String> tuple : allViewList) {

            Long meetingId = Long.parseLong(tuple.getValue());
            Long viewCount = tuple.getScore().longValue();

            increaseViewCount(meetingId, viewCount);
        }
    }

    @Transactional
    public void increaseViewCount(Long meetingId, Long viewCount) {

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        meeting.increaseViews(viewCount);
        meetingRepository.save(meeting);
    }
}
