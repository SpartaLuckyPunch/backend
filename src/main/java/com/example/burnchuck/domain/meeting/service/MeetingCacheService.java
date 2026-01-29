package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.entity.Meeting;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_GEO_KEY = "geoPoints:meeting";

    public void saveMeetingLocation(Meeting meeting) {

        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        Point point = new Point(meeting.getLongitude(), meeting.getLatitude());

        geoOperations.add(CACHE_GEO_KEY, point, String.valueOf(meeting.getId()));
    }

    public List<Long> findMeetingsByLocation(Location userLocation, double radius) {

        Circle searchArea = new Circle(new Point(userLocation.getLongitude(), userLocation.getLatitude()), new Distance(radius, RedisGeoCommands.DistanceUnit.KILOMETERS));

        GeoResults<GeoLocation<String>> geoResults = redisTemplate.opsForGeo().radius(CACHE_GEO_KEY, searchArea);

        if (geoResults == null) {
            return List.of();
        }

        return geoResults.getContent().stream()
            .map(r -> Long.parseLong(r.getContent().getName()))
            .collect(Collectors.toList());
    }

    public void deleteMeetingLocation(Long meetingId) {
        redisTemplate.opsForZSet().remove(CACHE_GEO_KEY, String.valueOf(meetingId));
    }
}
