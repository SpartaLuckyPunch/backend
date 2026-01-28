package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_GEO_KEY = "geoPoints";
    private static final String CACHE_GEO_PREFIX = "meeting:";

    public void saveMeetingLocation(Meeting meeting) {

        GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();

        Point point = new Point(meeting.getLongitude(), meeting.getLatitude());

        geoOperations.add(CACHE_GEO_KEY, point, CACHE_GEO_PREFIX + meeting.getId());
    }
}
