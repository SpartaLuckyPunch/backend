package com.example.burnchuck.common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meeting_likes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_meeting",
            columnNames = {"user_id", "meeting_id"}
        )
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    public MeetingLike(User user, Meeting meeting) {
        this.user = user;
        this.meeting = meeting;
    }
}
