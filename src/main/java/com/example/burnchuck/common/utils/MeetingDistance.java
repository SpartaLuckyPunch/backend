package com.example.burnchuck.common.utils;

import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.Direction;

public class MeetingDistance {

    // 탐색 거리(km 단위)
    private static final double SEARCH_DISTANCE = 5.0;

    /**
     * 기준 지점에서 최대 반경이 되는 북동쪽 좌표
     */
    public static Location aroundUserNortheastDot(Location userLocation) {

        double nowLatitude = userLocation.getLatitude();
        double nowLongitude = userLocation.getLongitude();

        return GeometryUtil.calculateByDirection(nowLatitude, nowLongitude, SEARCH_DISTANCE, Direction.NORTHEAST.getBearing());
    }

    /**
     * 기준 지점에서 최대 거리가 되는 남서쪽 좌표
     */
    public static Location aroundUserSouthwestDot(Location userLocation) {

        double nowLatitude = userLocation.getLatitude();
        double nowLongitude = userLocation.getLongitude();

        return GeometryUtil.calculateByDirection(nowLatitude, nowLongitude, SEARCH_DISTANCE, Direction.SOUTHWEST.getBearing());
    }

    /**
     * 유저와 모임 사이 거리 계산
     */
    public static double calculateDistance(Location userLocation, Meeting meeting) {

        double nowLatitude = userLocation.getLatitude();
        double nowLongitude = userLocation.getLongitude();

        double storeLatitude = meeting.getPoint().getY();
        double storeLongitude = meeting.getPoint().getX();

        return GeometryUtil.calculateDistance(nowLatitude, nowLongitude, storeLatitude, storeLongitude);
    }
}
