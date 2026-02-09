package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.RedisSyncFailure;
import com.example.burnchuck.common.enums.SyncType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RedisSyncFailureRepository extends JpaRepository<RedisSyncFailure, Long> {

    @Query("SELECT r FROM RedisSyncFailure r JOIN FETCH r.meeting WHERE r.syncType = :syncType")
    List<RedisSyncFailure> findAllWithMeetingsBySyncType(@Param("syncType") SyncType syncType);
}
