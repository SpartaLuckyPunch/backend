package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceFacade {
    private final AttendanceService attendanceService;
    private final RedissonClient redissonClient;

    public void registerAttendance(AuthUser authUser, Long meetingId) {
        String lockKey = "meeting_lock:" + meetingId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            log.info("[접근] 유저ID: {} 가 락 획득 시도", authUser.getId());

            boolean isLocked = lock.tryLock(5, TimeUnit.SECONDS);

            if (!isLocked) {
                log.error("[실패] 유저ID: {} 락 획득 실패", authUser.getId());
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

            log.info("[성공] 유저ID: {} 락 획득", authUser.getId());
            attendanceService.registerAttendance(authUser, meetingId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[해제] 유저ID: {} 락 반납 완료", authUser.getId());
            }
        }
    }
}
