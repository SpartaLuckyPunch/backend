package com.example.burnchuck.domain.scheduler.service;

import static com.example.burnchuck.domain.scheduler.repository.SchedulingRepository.MEETING_CHANGE_STATUS;
import static com.example.burnchuck.domain.scheduler.repository.SchedulingRepository.NOTIFICATION_REVIEW_REQUEST;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.scheduler.dto.SchedulingTask;
import com.example.burnchuck.domain.scheduler.repository.SchedulingRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final TaskScheduler taskScheduler;
    private final TransactionTemplate transactionTemplate;
    private final MeetingRepository meetingRepository;
    private final SchedulingRepository schedulingRepository;
    private final NotificationService notificationService;

    private <T> void scheduleTask(
        T target,
        Long targetId,
        String actionType,
        Consumer<T> action,
        LocalDateTime executionDatetime
    ) {

        SchedulingTask<T> task = new SchedulingTask<>(target, action, transactionTemplate);
        Instant execution = executionDatetime.atZone(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, execution);

        schedulingRepository.save(targetId, actionType, scheduledTask);
    }

    /**
     * 모임 상태 변경 스케줄 생성 (모임 시작 10분 전)
     */
    public void scheduleMeetingStatusComplete(Meeting meeting) {

        scheduleTask(
            meeting,
            meeting.getId(),
            MEETING_CHANGE_STATUS,
            e -> {
                // 영속성 컨텍스트에 올리기 위해 다시 조회
                Meeting targetMeeting = meetingRepository.findActivateMeetingById(meeting.getId());

                // 상태 변경
                targetMeeting.updateStatus(MeetingStatus.COMPLETED);
            },
            meeting.getMeetingDateTime().minusMinutes(10)
        );
    }

    /**
     * 알림 생성(후기 작성) 스케줄 생성 (모임 3시간 후)
     */
    public void scheduleNotification(Meeting meeting) {

        scheduleTask(
            meeting,
            meeting.getId(),
            NOTIFICATION_REVIEW_REQUEST,
            e -> {
                // 영속성 컨텍스트에 올리기 위해 다시 조회
                Meeting targetMeeting = meetingRepository.findActivateMeetingById(meeting.getId());

                // 알림 생성
                notificationService.notifyCommentRequest(targetMeeting);
            },
            meeting.getMeetingDateTime().plusHours(3)
        );
    }

    public void scheduleCancel(Long targetId) {

        schedulingRepository.cancel(targetId);
    }
}
