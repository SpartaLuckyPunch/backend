package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.user.repository.UserRepository;
import com.example.burnchuck.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockAttendanceFacade {

    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;
    private final AttendanceService attendanceService;
    private final RedissonClient redissonClient;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;


    public static final Long WAIT_TIME = 5L;

    public void registerAttendance(AuthUser authUser, Long meetingId) {
        String lockKey = "meeting_lock:" + meetingId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(WAIT_TIME, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

            attendanceService.registerAttendance(authUser, meetingId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
