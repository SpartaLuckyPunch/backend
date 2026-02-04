package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.utils.GeometryUtil;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import io.lettuce.core.RedisException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.RedisConnectionFailureException;
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
@Slf4j(topic = "MeetingRedisCache")
public class MeetingCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_GEO_KEY = "geoPoints:meeting";
    private static final String VIEW_COUNT_KEY = "view::meeting::";
    private static final int VIEW_COUNT_TTL = 3; // 1일 단위
    private static final String VIEW_COUNT_LOG_KEY = "view::meeting::%s::%s";
    private static final long VIEW_COUNT_LOG_TTL = 60 * 10 * 60; // TTL 1시간

    /**
     * 위치 정보 저장
     */
    public void saveMeetingLocation(Meeting meeting) {

        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();

        Point point = new Point(meeting.getLongitude(), meeting.getLatitude());

        try {
            geoOperations.add(CACHE_GEO_KEY, point, String.valueOf(meeting.getId()));
        } catch (RedisException | RedisConnectionFailureException e) {
            log.error("Redis 예외 발생: {}", e.getMessage());
        }
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

        try {
            redisTemplate.opsForZSet().remove(CACHE_GEO_KEY, String.valueOf(meetingId));
        } catch (RedisException | RedisConnectionFailureException e) {
            log.error("Redis 예외 발생: {}", e.getMessage());
        }
    }

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
}
