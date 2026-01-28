package com.example.burnchuck.common.utils;

import com.example.burnchuck.common.dto.BoundingBox;
import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.entity.Meeting;

public class MeetingDistance {

    /**
     * 반경 5km 원을 감싸는 최소 사각형 생성
     */
    public static BoundingBox aroundUserBox(Location userLocation, Double distance) {

        double lat = userLocation.getLatitude();
        double lng = userLocation.getLongitude();

        Location north = GeometryUtil.calculateByDirection(lat, lng, distance, 0.0);
        Location south = GeometryUtil.calculateByDirection(lat, lng, distance, 180.0);
        Location east  = GeometryUtil.calculateByDirection(lat, lng, distance, 90.0);
        Location west  = GeometryUtil.calculateByDirection(lat, lng, distance, 270.0);

        return new BoundingBox(
            south.getLatitude(),
            north.getLatitude(),
            west.getLongitude(),
            east.getLongitude()
        );
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
