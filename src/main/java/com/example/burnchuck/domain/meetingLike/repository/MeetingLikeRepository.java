package com.example.burnchuck.domain.meetingLike.repository;

import com.example.burnchuck.common.entity.MeetingLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingLikeRepository extends JpaRepository<MeetingLike, Long> {

    void deleteByUserId(Long id);
}
