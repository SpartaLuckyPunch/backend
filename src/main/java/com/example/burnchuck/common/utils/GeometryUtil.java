package com.example.burnchuck.common.utils;

import com.example.burnchuck.common.dto.Location;

public class GeometryUtil {

    private static final double EARTH_RADIUS = 6371.01;

    /**
     * 위도, 경도, 거리, 방향 입력 -> 해당 위경도 기준으로 방향과 거리만큼 떨어진 좌표 계산
     */
    public static Location calculateByDirection(Double baseLatitude, Double baseLongitude, Double distance, Double bearing) {

        Double radianLatitude = toRadian(baseLatitude);
        Double radianLongitude = toRadian(baseLongitude);
        Double radianAngle = toRadian(bearing);
        Double distanceRadius = distance / EARTH_RADIUS;

        Double latitude = Math.asin(sin(radianLatitude) * cos(distanceRadius) + cos(radianLatitude) * sin(distanceRadius) * cos(radianAngle));

        Double longitude = radianLongitude + Math.atan2(sin(radianAngle) * sin(distanceRadius) * cos(radianLatitude), cos(distanceRadius) - sin(radianLatitude) * sin(latitude));
        longitude = normalizeLongitude(longitude);

        return new Location(toDegree(latitude), toDegree(longitude));
    }

    /**
     * 두 개의 위도, 경도 입력 시, 거리 계산
     */
    public static double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {

        double dLat = toRadian(lat2 - lat1);
        double dLon = toRadian(lon2 - lon1);

        double a = sin(dLat / 2) * sin(dLat / 2) + Math.cos(toRadian(lat1)) * cos(toRadian(lat2)) * sin(dLon / 2) * sin(dLon / 2);
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * b * 1000; //m
    }

    private static Double toRadian(Double coordinate) {
        return coordinate * Math.PI / 180.0;
    }

    private static Double toDegree(Double coordinate) {
        return coordinate * 180.0 / Math.PI;
    }

    private static Double sin(Double coordinate) {
        return Math.sin(coordinate);
    }

    private static Double cos(Double coordinate) {
        return Math.cos(coordinate);
    }

    private static Double normalizeLongitude(Double longitude) {
        return (longitude + 540) % 360 - 180;
    }
}
