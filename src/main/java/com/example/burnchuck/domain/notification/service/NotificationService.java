package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.Notification;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.domain.attendance.repository.UserMeetingRepository;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.notification.model.response.NotificationGetListResponse;
import com.example.burnchuck.domain.notification.model.response.NotificationResponse;
import com.example.burnchuck.domain.notification.repository.NotificationRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final UserRepository userRepository;

    /**
     * 유저가 모임을 생성했을 때 -> 해당 유저를 팔로우하는 사람에게 알림 발송
     */
    @Transactional
    public void notifyNewFollowerPost(Meeting meeting, User user) {

        NotificationType notificationType = NotificationType.NEW_FOLLOWING_POST;

        // 1. 모임을 생성한 유저의 팔로워 목록 조회
        List<Follow> followerList = followRepository.findAllByFollowee(user);

        // 2. 생성한 meeting 내용에 맞게 알림 설명글 수정
        String description = notificationType.getDescription();
        description = description.replace("{nickname}", user.getNickname());
        description = description.replace("{title}", meeting.getTitle());

        // 3. 팔로워 리스트 순회하며 대상 유저를 넣어 알림 객체 생성
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

        // 4. 알림 저장
        notificationRepository.saveAll(notificationList);
    }

    /**
     * 모임에 새로운 유저가 추가되었을 때 -> 해당 모임의 주최자에게 알림 발송
     * 모임의 유저가 탈퇴했을 때 -> 해당 모임의 주최자에게 알림 발송
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyMeetingMember(boolean join, Meeting meeting, User participant) {

        // 1. 유저 추가 / 탈퇴 구분
        NotificationType notificationType;

        if (join) {
            notificationType = NotificationType.MEETING_MEMBER_JOIN;
        } else {
            notificationType = NotificationType.MEETING_MEMBER_LEFT;
        }

        // 2. 상황에 맞게 알림 설명글 수정
        String description = notificationType.getDescription();
        description = description.replace("{title}", meeting.getTitle());
        description = description.replace("{nickname}", participant.getNickname());

        // 3. Meeting의 HOST 조회
        UserMeeting host = userMeetingRepository.findHostByMeeting(meeting);

        // 4. Notification 객체 생성 및 저장
        Notification notification = new Notification(
            notificationType,
            description,
            host.getUser(),
            meeting
        );

        notificationRepository.save(notification);
    }

    /**
     * 후기 작성 안내 -> 모임 시작 시간 3시간 뒤, 모임 참석자들에게 발송
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyCommentRequest(Meeting meeting) {

        NotificationType notificationType = NotificationType.COMMENT_REQUESTED;

        // 1. 해당 모임의 참석자 조회
        List<UserMeeting> userMeetingList = userMeetingRepository.findMeetingMembers(meeting.getId());

        // 2. 상황에 맞게 알림 설명글 수정
        String description = notificationType.getDescription();
        description = description.replace("{title}", meeting.getTitle());

        // 3. 리스트 순회하며 알림 생성
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

        // 4. 알림 저장
        notificationRepository.saveAll(notificationList);
    }

    /**
     * 알림 목록 조회 (로그인한 유저 기준)
     */
    @Transactional(readOnly = true)
    public NotificationGetListResponse getNotificationList(AuthUser authUser) {

        // 1. 로그인한 유저 정보로 객체 생성
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 유저 기준 알림 목록 조회
        List<NotificationResponse> notificaionList = notificationRepository.findAllNotificationsByUser(user);

        return new NotificationGetListResponse(notificaionList);
    }

    /**
     * 알림 단건 조회 (알림 읽음 처리)
     */
    @Transactional
    public NotificationResponse readNotification(Long notificationId) {

        // 1. 알림 객체 생성
        Notification notification = notificationRepository.findNotificationById(notificationId);

        // 2. 알림 읽음 처리
        notification.read();

        return NotificationResponse.from(notification);
    }
}
