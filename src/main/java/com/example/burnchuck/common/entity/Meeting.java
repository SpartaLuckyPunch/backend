package com.example.burnchuck.common.entity;

import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.domain.meeting.dto.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingUpdateRequest;
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

    public Meeting(MeetingCreateRequest request, Category category) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.imgUrl = request.getImgUrl();
        this.location = request.getLocation();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.maxAttendees = request.getMaxAttendees();
        this.meetingDateTime = request.getMeetingDateTime();
        this.views = 0L;
        this.status = MeetingStatus.OPEN;
        this.category = category;
    }

    public void updateMeeting(MeetingUpdateRequest request, Category category) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.imgUrl = request.getImgUrl();
        this.location = request.getLocation();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.maxAttendees = request.getMaxAttendees();
        this.meetingDateTime = request.getMeetingDateTime();
        this.category = category;
    }

    public void increaseViews() {
        this.views++;
    }

    public void updateStatus(MeetingStatus status) {
        this.status = status;
    }
}
