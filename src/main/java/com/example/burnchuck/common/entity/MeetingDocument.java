package com.example.burnchuck.common.entity;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "meetings")
public class MeetingDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String categoryCode;

    private GeoPoint geoPoint;

    @Field(type = FieldType.Date)
    private LocalDate meetingDate;

    @Field(type = FieldType.Integer)
    private Integer meetingTime;

    public MeetingDocument(Meeting meeting) {
        this.id = String.valueOf(meeting.getId());
        this.title = meeting.getTitle();
        this.categoryCode = meeting.getCategory().getCode();
        this.geoPoint = new GeoPoint(meeting.getLatitude(), meeting.getLongitude());
        this.meetingDate = meeting.getMeetingDateTime().toLocalDate();
        this.meetingTime = meeting.getMeetingDateTime().getHour();
    }
}
