package com.example.burnchuck.fixture;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Meeting;
import java.time.LocalDateTime;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class MeetingFixture {

    public static final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
    public static final Category category = new Category("testcategory", "testcategory");
    public static Meeting testMeeting() {

        Point point = factory.createPoint(new Coordinate(37.5450159, 127.1368066));
        point.setSRID(4326);

        return new Meeting(
            "테스트",
            "테스트용 모임 생성",
            "www.test.com/test.png",
            "서울시 강동구 천호동",
            37.5450159,
            127.1368066,
            point,
            5,
            LocalDateTime.now().plusDays(3),
            category
        );
    }
}
