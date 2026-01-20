package com.example.burnchuck.domain.scheduler.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.scheduler.model.dto.SchedulingTask;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private <T> void scheduleTask(T target, Consumer<T> action, LocalDateTime executionDatetime) {

        SchedulingTask<T> task = new SchedulingTask<>(target, action, transactionTemplate);

        Instant execution = executionDatetime.atZone(ZoneId.systemDefault()).toInstant();

        taskScheduler.schedule(task, execution);
    }

    public void scheduleMeetingStatusComplete(Meeting meeting) {

        scheduleTask(
            meeting,
            e -> {
                // 영속성 컨텍스트에 올리기 위해 다시 조회
                Meeting targetMeeting = meetingRepository.findActivateMeetingById(meeting.getId());

                // 상태 변경
                targetMeeting.updateStatus(MeetingStatus.COMPLETED);
            },
            meeting.getMeetingDateTime().minusMinutes(1)
        );
    }
}
