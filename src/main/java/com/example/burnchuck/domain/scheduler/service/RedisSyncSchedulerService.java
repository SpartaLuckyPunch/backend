package com.example.burnchuck.domain.scheduler.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.RedisSyncFailure;
import com.example.burnchuck.common.enums.SyncType;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.RedisSyncFailureRepository;
import com.example.burnchuck.domain.meeting.service.MeetingCacheService;
import io.lettuce.core.RedisException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j(topic = "RedisSyncScheduler")
public class RedisSyncSchedulerService {

    private final RedisSyncFailureRepository redisSyncFailureRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingCacheService meetingCacheService;

    /**
     * Redis 장애로 누락된 CREATE 복구
     */
    @Scheduled(fixedDelay = 600000)
    public void createSync() {

        List<RedisSyncFailure> createSyncList = redisSyncFailureRepository.findAllWithMeetingsBySyncType(SyncType.CREATE);

        if (createSyncList.isEmpty()) {
            return;
        }

        for (RedisSyncFailure redisSyncFailure : createSyncList) {
            try {
                saveCache(redisSyncFailure);
            } catch (RedisConnectionFailureException e) {
                break;
            } catch (RedisException ignored) {
            }
        }
    }

    @Transactional
    void saveCache(RedisSyncFailure redisSyncFailure) {

        meetingCacheService.saveMeetingLocation(redisSyncFailure.getMeeting());
        redisSyncFailureRepository.delete(redisSyncFailure);
    }

    /**
     * Redis 장애로 누락된 DELETE 복구
     */
    @Scheduled(fixedDelay = 3600000)
    public void deleteSync() {

        List<RedisSyncFailure> deleteSyncList = redisSyncFailureRepository.findAllWithMeetingsBySyncType(SyncType.DELETE);

        if (deleteSyncList.isEmpty()) {
            return;
        }

        for (RedisSyncFailure redisSyncFailure : deleteSyncList) {
            try {
                deleteCache(redisSyncFailure);
            } catch (RedisConnectionFailureException e) {
                break;
            } catch (RedisException ignored) {
            }
        }
    }

    @Transactional
    void deleteCache(RedisSyncFailure redisSyncFailure) {

        meetingCacheService.deleteMeetingLocation(redisSyncFailure.getMeeting().getId());
        redisSyncFailureRepository.delete(redisSyncFailure);
    }

    /**
     * 매일 오전 4시 DB와 레디스 동기화
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void dailySync() {

        List<Meeting> meetingList = meetingRepository.findActivateMeetingsForSchedules();
        Set<Long> meetingIdSet = meetingList.stream().map(Meeting::getId).collect(Collectors.toSet());

        Set<Long> redisMeetingIdSet = meetingCacheService.findAll();

        meetingList.stream()
            .filter(meeting -> !redisMeetingIdSet.contains(meeting.getId()))
            .forEach(meetingCacheService::saveMeetingLocation);

        redisMeetingIdSet.stream()
            .filter(redisMeetingId -> !meetingIdSet.contains(redisMeetingId))
            .forEach(meetingCacheService::deleteMeetingLocation);
    }
}
