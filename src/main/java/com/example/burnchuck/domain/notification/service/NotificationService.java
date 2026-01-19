package com.example.burnchuck.domain.notification.service;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.Notification;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.domain.follow.repository.FollowRepository;
import com.example.burnchuck.domain.notification.repository.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;

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
}
