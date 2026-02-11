package com.example.burnchuck.domain.meetingLike.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingLikeCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LIKE_COUNT_KEY = "like::meeting::%s";
    private static final int LIKE_COUNT_TTL = 3; // 1일 단위

    public void increaseMeetingLike(Long meetingId) {

        String key = generateKey();
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(meetingId), 1);

        redisTemplate.expire(key, LIKE_COUNT_TTL, TimeUnit.DAYS);
    }

    public void decreaseMeetingLike(Long meetingId) {

        String key = generateKey();
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(meetingId), -1);

        redisTemplate.expire(key, LIKE_COUNT_TTL, TimeUnit.DAYS);
    }

    public String generateKey() {
        return String.format(LIKE_COUNT_KEY, LocalDate.now());
    }

    public Set<TypedTuple<String>> getLikeKeyList() {

        String key = generateKey();

        if (!redisTemplate.hasKey(key)) {
            return Collections.emptySet();
        }

        return redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
    }

    public void clearLikeKey(Set<Long> meetingIdList) {

        for (Long meetingId : meetingIdList) {
            redisTemplate.opsForZSet().remove(generateKey(), String.valueOf(meetingId));
        }
    }
}
