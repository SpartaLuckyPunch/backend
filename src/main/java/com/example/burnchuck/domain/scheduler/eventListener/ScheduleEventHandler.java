package com.example.burnchuck.domain.scheduler.eventListener;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.scheduler.model.dto.MeetingCreatedEvent;
import com.example.burnchuck.domain.scheduler.model.dto.MeetingDeletedEvent;
import com.example.burnchuck.domain.scheduler.model.dto.MeetingUpdatedEvent;
import com.example.burnchuck.domain.scheduler.service.SchedulingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "ScheduleEventHandler")
public class ScheduleEventHandler {

    private final SchedulingService schedulingService;
    private final MeetingRepository meetingRepository;

    /**
     * 어플리케이션 재시작 후, 삭제된 이벤트 복구
     */
    @EventListener(ApplicationReadyEvent.class)
    public void restoreSchedules() {

        // COMPLETED 되지 않은 meeting 조회
        List<Meeting> meetingList = meetingRepository.findActivateMeetingByStatusNot(MeetingStatus.COMPLETED);

        // task 등록
        meetingList.forEach(meeting -> {
            schedulingService.scheduleMeetingStatusComplete(meeting);
            schedulingService.scheduleNotification(meeting);
        });
    }

    /**
     * MeetingCreatedEvent에 대한 Handler -> TaskSchedule 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    public void meetingCreateScheduleEventHandler(MeetingCreatedEvent event) {

        Meeting meeting = event.getMeeting();

        try {
            schedulingService.scheduleMeetingStatusComplete(meeting);
            schedulingService.scheduleNotification(meeting);
        } catch (Exception e) {
            log.error("스케줄러 생성 실패 : {}", meeting.getId());
        }
    }

    /**
     * MeetingUpdatedEvent에 대한 Handler -> 기존 TaskSchedule 취소 및 새 작업 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void meetingUpdateScheduleEventHandler(MeetingUpdatedEvent event) {

        Meeting meeting = event.getMeeting();

        try {
            // 1. 예정된 작업 취소
            schedulingService.scheduleCancel(meeting.getId());

            // 2. 새로운 작업 생성
            schedulingService.scheduleMeetingStatusComplete(meeting);
            schedulingService.scheduleNotification(meeting);
        } catch (Exception e) {
            log.error("스케줄러 생성 실패 : {}", meeting.getId());
        }
    }

    /**
     * MeetingDeletedEvent에 대한 Handler -> 기존 TaskSchedule 취소
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void meetingDeleteScheduleEventHandler(MeetingDeletedEvent event) {

        Meeting meeting = event.getMeeting();

        try {
            schedulingService.scheduleCancel(meeting.getId());
        } catch (Exception e) {
            log.error("스케줄러 생성 실패 : {}", meeting.getId());
        }
    }
}
