package com.example.burnchuck.common.entity;

import com.example.burnchuck.common.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications",
    indexes = {
        @Index(
            name = "idx_unread_count",
            columnList = "user_id, is_read, notified_datetime"
        )
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    public Notification(NotificationType type, String description, User user, Meeting meeting) {
        this.type = type;
        this.description = description;
        this.user = user;
        this.meeting = meeting;
    }

    public void read() {
        this.isRead = true;
    }
}
