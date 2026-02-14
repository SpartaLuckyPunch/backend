package com.example.burnchuck.common.entity;

import com.example.burnchuck.common.enums.MeetingRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_meetings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingRole meetingRole;

    public UserMeeting(User user, Meeting meeting, MeetingRole meetingRole) {
        this.user = user;
        this.meeting = meeting;
        this.meetingRole = meetingRole;
    }

    public boolean isHost() {
        return this.meetingRole == MeetingRole.HOST;
    }
}
