package com.example.burnchuck.domain.meeting.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "MeetingRedisCache")
public class MeetingCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String VIEW_COUNT_KEY = "view::meeting::";
    private static final int VIEW_COUNT_TTL = 3; // 1일 단위
    private static final String VIEW_COUNT_LOG_KEY = "view::meeting::%s::%s";
    private static final long VIEW_COUNT_LOG_TTL = 6 * 10 * 60; // TTL 1시간

    /**
     * 조회수 1 증가
     */
    public void increaseViewCount(String ipAddress, Long meetingId) {

        if (isCountable(ipAddress, meetingId)) {

            saveViewRecord(ipAddress, meetingId);

            String key = VIEW_COUNT_KEY + LocalDate.now();
            redisTemplate.opsForZSet().incrementScore(key, String.valueOf(meetingId), 1);

            redisTemplate.expire(key, VIEW_COUNT_TTL, TimeUnit.DAYS);
        }
    }

    /**
     * 해당 IP로 조회힌 내역이 있는지 확인
     */
    public boolean isCountable(String ipAddress, Long meetingId) {

        String key = generateKey(meetingId, ipAddress);

        Boolean exists = redisTemplate.hasKey(key);
        return !Boolean.TRUE.equals(exists);
    }

    /**
     * 모임ID와 조회한 IP 기록
     */
    private void saveViewRecord(String ipAddress, Long meetingId) {

        String key = generateKey(meetingId, ipAddress);

        redisTemplate.opsForValue()
            .setIfAbsent(key, "1", VIEW_COUNT_LOG_TTL, TimeUnit.SECONDS);
    }

    private String generateKey(Long meetingId, String ipAddress) {
        return String.format(VIEW_COUNT_LOG_KEY, meetingId, ipAddress);
    }

    /**
     * 모임의 조회수 조회
     */
    public Double getViewCount(Long meetingId) {

        String key = VIEW_COUNT_KEY + LocalDate.now();

        Double viewCount = redisTemplate.opsForZSet().score(key, String.valueOf(meetingId));

        return viewCount == null ? 0 : viewCount;
    }

    public Set<TypedTuple<String>> getAllViewList(LocalDate localDate) {

        String key = VIEW_COUNT_KEY + localDate;

        if (!redisTemplate.hasKey(key)) {
            return Collections.emptySet();
        }

        return redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
    }
}
