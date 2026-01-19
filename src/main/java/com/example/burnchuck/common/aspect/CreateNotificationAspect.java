package com.example.burnchuck.common.aspect;

import com.example.burnchuck.common.annotation.CreateNotification;
import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.Notification;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j(topic = "notification")
@RequiredArgsConstructor
public class CreateNotificationAspect {

    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    @AfterReturning(value = "@annotation(createNotification)", returning = "returnObj")
    public void createNotification(
        JoinPoint joinPoint,
        CreateNotification createNotification,
        Object returnObj
    ) {

        NotificationType notificationType = createNotification.notificationType();

        Object[] args = joinPoint.getArgs();

        List<Notification> notificationList = switch (notificationType) {
            case NEW_FOLLOWING_POST -> notificateNewFollowerPost(notificationType, args, returnObj);
            case MEETING_MEMBER_JOIN -> notificateMeetingMemberJoin(args);
            case MEETING_MEMBER_LEFT -> notificateMeetingMemberLeft(args);
            case COMMENT_REQUESTED -> notificateCommentRequest(args);
        };

        notificationService.createNotification(notificationList);
    }

    /**
     * 유저가 모임을 생성했을 때 -> 해당 유저를 팔로우하는 사람에게 알림 발송
     */
    private List<Notification> notificateNewFollowerPost(
        NotificationType notificationType,
        Object[] args,
        Object returnObj
    ) {

        List<Notification> result = new ArrayList<>();

        // 1. 생성된 모임 객체 생성
        // TODO: returnObj를 반환 DTO로 바꿔서, 반환되는 id 값 추출
        Long meetingId = 1L;
        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        // 2. 생성한 유저 객체 생성
        // TODO : JoinPoint 파라미터에서 로그인된 유저ID 추출
        Long userId = 1L;
        User loginUser = userRepository.findActivateUserById(userId);

        // 3. 로그인 유저의 팔로워 목록 조회
        List<Follow> followerList = followRepository.findAllByFollowee(loginUser);

        // 4. 알림 객체 생성
        String description = notificationType.getDescription().replace("{title}", meeting.getTitle());

        for (Follow follow : followerList) {

            Notification notification = new Notification(notificationType, description, follow.getFollower(), meeting);

            result.add(notification);
        }

        return result;
    }

    /**
     * 모임에 새로운 유저가 추가되었을 때 -> 해당 모임의 주최자에게 알림 발송
     */
    private List<Notification> notificateMeetingMemberJoin(Object[] args) {

        List<Notification> result = new ArrayList<>();

        return result;
    }

    /**
     * 모임의 유저가 탈퇴했을 때 -> 해당 모임의 주최자에게 알림 발송
     */
    private List<Notification> notificateMeetingMemberLeft(Object[] args) {

        List<Notification> result = new ArrayList<>();

        return result;
    }

    /**
     * 댓글 작성 요청 -> 모임이 시작된지 3시간이 지난 후, 해당 모임의 참석자에게 알림 발송
     */
    private List<Notification> notificateCommentRequest(Object[] args) {

        List<Notification> result = new ArrayList<>();

        return result;
    }
}
