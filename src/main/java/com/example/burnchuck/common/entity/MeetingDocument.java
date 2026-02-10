package com.example.burnchuck.common.entity;

import com.example.burnchuck.common.enums.MeetingStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
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

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime meetingDatetime;

    @Field(type = FieldType.Integer)
    private Integer meetingHour;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdDatetime;

    @Field(type = FieldType.Keyword)
    private MeetingStatus status;

    @Field(type = FieldType.Keyword)
    private String imgUrl;

    @Field(type = FieldType.Keyword)
    private String location;

    @Field(type = FieldType.Integer)
    private int maxAttendees;

    public MeetingDocument(Meeting meeting) {
        this.id = String.valueOf(meeting.getId());
        this.title = meeting.getTitle();
        this.categoryCode = meeting.getCategory().getCode();
        this.geoPoint = new GeoPoint(meeting.getLatitude(), meeting.getLongitude());
        this.meetingDatetime = meeting.getMeetingDateTime();
        this.meetingHour = meeting.getMeetingDateTime().getHour();
        this.createdDatetime = meeting.getCreatedDatetime();
        this.status = meeting.getStatus();
        this.imgUrl = meeting.getImgUrl();
        this.location = meeting.getLocation();
        this.maxAttendees = meeting.getMaxAttendees();
    }
}
