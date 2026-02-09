package com.example.burnchuck.common.entity;

import com.example.burnchuck.common.enums.SyncType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "redis_sync_failures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisSyncFailure extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncType syncType;

    public RedisSyncFailure(Meeting meeting, SyncType syncType) {
        this.meeting = meeting;
        this.syncType = syncType;
    }

    public boolean isCreate() {
        return this.syncType == SyncType.CREATE;
    }

    public boolean isDelete() {
        return this.syncType == SyncType.DELETE;
    }
}
