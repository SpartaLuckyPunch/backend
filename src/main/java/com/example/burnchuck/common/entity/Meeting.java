package com.example.burnchuck.common.entity;

import com.example.burnchuck.common.enums.MeetingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "meetings")
@Getter
@NoArgsConstructor
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private int maxAttendees;

    @Column(nullable = false)
    private LocalDateTime meetingDateTime;

    @Column(nullable = false)
    private long views;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Meeting(String title, String description, String imgUrl, String location, Double latitude, Double longitude, int maxAttendees, LocalDateTime meetingDateTime, MeetingStatus status, Category category) {
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxAttendees = maxAttendees;
        this.meetingDateTime = meetingDateTime;
        this.views = 0L;
        this.status = status;
        this.category = category;
    }

    public void updateMeeting(String title, String description, String imgUrl, String location, Double latitude, Double longitude, int maxAttendees, LocalDateTime meetingDateTime, Category category) {
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxAttendees = maxAttendees;
        this.meetingDateTime = meetingDateTime;
        this.category = category;
    }

    public void increaseViews() {
        this.views++;
    }

    public void updateStatus(MeetingStatus status) {
        this.status = status;
    }
}
