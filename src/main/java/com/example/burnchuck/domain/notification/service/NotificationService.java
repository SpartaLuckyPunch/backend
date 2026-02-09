package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.Notification;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.notification.dto.response.NotificationGetListResponse;
import com.example.burnchuck.domain.notification.dto.response.NotificationResponse;
import com.example.burnchuck.domain.notification.dto.response.NotificationSseResponse;
import com.example.burnchuck.domain.notification.repository.NotificationRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final UserRepository userRepository;

    private final SseNotifyService sseNotifyService;
    private final RedisMessageService redisMessageService;
    private final EmitterService emitterService;

    /**
     * 클라이언트와의 SSE 스트림 통신 연결
     */
    public SseEmitter subscribe(AuthUser authUser) {

        Long userId = authUser.getId();

        SseEmitter emitter = emitterService.createEmitter(userId);

        emitter.onCompletion(() -> {
            emitterService.deleteEmitter(userId);
            redisMessageService.removeSubscribe(userId);
        });
        emitter.onTimeout(emitter::complete);

        long unread = notificationRepository.countByUserIdAndIsReadFalse(userId);

        sseNotifyService.send(emitter, userId, NotificationSseResponse.sseConnection(unread));

        redisMessageService.subscribe(userId);

        return emitter;
    }

    /**
     * 해당 알림을 수신하는 모든 유저에게 전송
     */
    private void publishNotificationList(List<Notification> notificationList) {

        for (Notification notification : notificationList) {

            Long userId = notification.getUser().getId();
            NotificationResponse notificationResponse = NotificationResponse.from(notification);

            redisMessageService.publish(userId, notificationResponse);
        }
    }

    /**
     * 앞선 내용 커밋 후 실행
     */
    private void sendAllAfterCommit(List<Notification> notificationList) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publishNotificationList(notificationList);
            }
        });
    }

    /**
     * 유저가 모임을 생성했을 때 -> 해당 유저를 팔로우하는 사람에게 알림 발송
     */
    @Async("CustomTaskExecutor")
    @Transactional
    public void notifyNewFollowerPost(Meeting meeting, User user) {

        NotificationType notificationType = NotificationType.NEW_FOLLOWING_POST;

        List<Follow> followerList = followRepository.findAllByFollowee(user);

        String description = notificationType.getDescription(notificationType, meeting.getTitle(), user.getNickname());

        List<Notification> notificationList = new ArrayList<>();

        for (Follow follow : followerList) {

            Notification notification = new Notification(
                notificationType,
                description,
                follow.getFollower(),
                meeting
            );

            notificationList.add(notification);
        }

        notificationRepository.saveAll(notificationList);

        sendAllAfterCommit(notificationList);
    }

    /**
     * 모임에 새로운 유저가 추가되었을 때 -> 해당 모임의 주최자에게 알림 발송
     * 모임의 유저가 탈퇴했을 때 -> 해당 모임의 주최자에게 알림 발송
     */
    @Async("CustomTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyMeetingMember(NotificationType notificationType, Meeting meeting, User attendee) {

        String description = notificationType.getDescription(notificationType, meeting.getTitle(), attendee.getNickname());

        UserMeeting host = userMeetingRepository.findHostByMeeting(meeting);

        Notification notification = new Notification(
            notificationType,
            description,
            host.getUser(),
            meeting
        );

        notificationRepository.save(notification);

        sendAllAfterCommit(List.of(notification));
    }

    /**
     * 후기 작성 안내 -> 모임 시작 시간 3시간 뒤, 모임 참석자들에게 발송
     */
    @Async("CustomTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyCommentRequest(Meeting meeting) {

        NotificationType notificationType = NotificationType.COMMENT_REQUESTED;

        List<UserMeeting> userMeetingList = userMeetingRepository.findMeetingMembers(meeting.getId());

        String description = notificationType.getDescription(notificationType, meeting.getTitle(), null);

        List<Notification> notificationList = new ArrayList<>();

        for (UserMeeting userMeeting : userMeetingList) {

            Notification notification = new Notification(
                notificationType,
                description,
                userMeeting.getUser(),
                meeting
            );

            notificationList.add(notification);
        }

        notificationRepository.saveAll(notificationList);

        sendAllAfterCommit(notificationList);
    }

    /**
     * 알림 목록 조회 (로그인한 유저 기준)
     */
    @Transactional(readOnly = true)
    public NotificationGetListResponse getNotificationList(AuthUser authUser) {

        User user = userRepository.findActivateUserById(authUser.getId());

        LocalDateTime sevenDaysAgo = LocalDate.now().atStartOfDay().minusDays(7);

        List<NotificationResponse> notificaionList = notificationRepository.findAllNotificationsByUser(user, sevenDaysAgo);

        return new NotificationGetListResponse(notificaionList);
    }

    /**
     * 알림 단건 조회 (알림 읽음 처리)
     */
    @Transactional
    public NotificationResponse readNotification(Long notificationId) {

        Notification notification = notificationRepository.findNotificationById(notificationId);

        notification.read();

        return NotificationResponse.from(notification);
    }
}
