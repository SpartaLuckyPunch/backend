package com.example.burnchuck.domain.meetingLike.service;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingLikeCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String LIKE_COUNT_KEY = "like::meeting::%s::%s";
    private static final int LIKE_COUNT_TTL = 2; // 1일 단위

    public void increaseMeetingLike(Long meetingId) {

        String key = generateKey(meetingId);
        redisTemplate.opsForValue().increment(key);

        redisTemplate.expire(key, LIKE_COUNT_TTL, TimeUnit.DAYS);
    }

    public void decreaseMeetingLike(Long meetingId) {

        String key = generateKey(meetingId);
        redisTemplate.opsForValue().decrement(key);

        redisTemplate.expire(key, LIKE_COUNT_TTL, TimeUnit.DAYS);
    }

    public String generateKey(Long meetingId) {
        return String.format(LIKE_COUNT_KEY, LocalDate.now(), meetingId);
    }

    public Set<Long> getLikeKeyList() {

        Set<String> keys = redisTemplate.keys("like::meeting::" + LocalDate.now());

        return keys.stream()
            .map(this::getMeetingId)
            .collect(Collectors.toSet());
    }

    private Long getMeetingId(String key) {

        String prefix = "like::meeting::" + LocalDate.now();
        return Long.parseLong(key.substring(prefix.length()));
    }

    public void clearLikeKey(Set<Long> meetingIdList) {

        for (Long meetingId : meetingIdList) {
            redisTemplate.delete(generateKey(meetingId));
        }
    }
}
