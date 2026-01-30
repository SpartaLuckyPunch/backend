package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.utils.GeometryUtil;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoSearchCommandArgs;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.BoundingBox;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_GEO_KEY = "meetings:geo";

    /**
     * 위치 정보 저장
     */
    public void saveMeetingLocation(Meeting meeting) {

        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        Point point = new Point(meeting.getLongitude(), meeting.getLatitude());

        geoOperations.add(CACHE_GEO_KEY, point, String.valueOf(meeting.getId()));
    }

    /**
     * 유저 위치 주변 모임 조회(거리 기준 오름차순 정렬)
     */
    public List<Long> findMeetingsByLocation(Location userLocation, double radius) {

        Circle searchArea = new Circle(new Point(userLocation.getLongitude(), userLocation.getLatitude()), new Distance(radius, RedisGeoCommands.DistanceUnit.KILOMETERS));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().sortAscending();

        GeoResults<GeoLocation<String>> geoResults = redisTemplate.opsForGeo().radius(CACHE_GEO_KEY, searchArea, args);

        if (geoResults == null) {
            return List.of();
        }

        return geoResults.getContent().stream()
            .map(r -> Long.parseLong(r.getContent().getName()))
            .collect(Collectors.toList());
    }

    public List<Long> findMeetingsByViewPort(MeetingMapViewPortRequest viewPort) {

        GeoReference reference = GeoReference.fromCoordinate(viewPort.getCenterLng(), viewPort.getCenterLat());

        GeoSearchCommandArgs args = GeoSearchCommandArgs.newGeoSearchArgs().sortAscending();

        Double height = GeometryUtil.calculateDistance(viewPort.getMinLat(), viewPort.getCenterLng(), viewPort.getMaxLat(), viewPort.getCenterLng());
        Double width = GeometryUtil.calculateDistance(viewPort.getCenterLat(), viewPort.getMinLng(), viewPort.getCenterLat(), viewPort.getMaxLng());

        BoundingBox boundingBox = new BoundingBox(new Distance(width), new Distance(height));

        GeoResults<GeoLocation<String>> geoResults = redisTemplate.opsForGeo().search(CACHE_GEO_KEY, reference, boundingBox, args);

        if (geoResults == null) {
            return List.of();
        }

        return geoResults.getContent().stream()
            .map(r -> Long.parseLong(r.getContent().getName()))
            .collect(Collectors.toList());
    }

    /**
     * 저장된 내용 삭제
     */
    public void deleteMeetingLocation(Long meetingId) {
        redisTemplate.opsForZSet().remove(CACHE_GEO_KEY, String.valueOf(meetingId));
    }
}
