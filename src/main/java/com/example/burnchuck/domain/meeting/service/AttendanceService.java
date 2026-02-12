package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.chat.repository.ChatRoomRepository;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.meeting.dto.response.AttendanceGetMeetingListResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
import com.example.burnchuck.domain.meeting.event.EventPublisherService;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.user.repository.UserRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final UserMeetingRepository userMeetingRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final NotificationService notificationService;
    private final ChatRoomService chatRoomService;
    private final EventPublisherService eventPublisherService;

    /**
     * 모임 참여 신청
     */
    @Transactional
    public void registerAttendance(AuthUser authUser, Long meetingId) {

        User user = userRepository.findActivateUserById(authUser.getId());
        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        if (!meeting.isOpen()) {
            throw new CustomException(ErrorCode.ATTENDANCE_CANNOT_REGISTER);
        }

        if (userMeetingRepository.existsByUserIdAndMeetingId(user.getId(), meeting.getId())) {
            throw new CustomException(ErrorCode.ATTENDANCE_ALREADY_REGISTERED);
        }

        int maxAttendees = meeting.getMaxAttendees();
        int currentAttendees = userMeetingRepository.countByMeeting(meeting);

        if (currentAttendees >= maxAttendees) {
            throw new CustomException(ErrorCode.ATTENDANCE_MAX_CAPACITY_REACHED);
        }

        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.PARTICIPANT);

        userMeetingRepository.save(userMeeting);

        if (currentAttendees +1 == maxAttendees) {
            meeting.updateStatus(MeetingStatus.CLOSED);
            eventPublisherService.publishMeetingStatusChangeEvent(meeting, MeetingStatus.CLOSED);
        }

        chatRoomService.joinGroupChatRoom(meetingId, user);

        notificationService.notifyMeetingMember(NotificationType.MEETING_MEMBER_JOIN, meeting, user);

        eventPublisherService.publishMeetingAttendeesChangeEvent(meeting);
    }

    /**
     * 모임 참여 취소
     */
    @Transactional
    public void cancelAttendance(AuthUser authUser, Long meetingId) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        if (meeting.isCompleted()) {
            throw new CustomException(ErrorCode.ATTENDANCE_CANNOT_CANCEL_WHEN_MEETING_CLOSED);
        }

        UserMeeting userMeeting = userMeetingRepository.findUserMeeting(user.getId(), meeting.getId());

        if (userMeeting.isHost()) {
            throw new CustomException(ErrorCode.ATTENDANCE_HOST_CANNOT_CANCEL);
        }

        userMeetingRepository.delete(userMeeting);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByMeetingId(meetingId);

        chatRoomService.leaveChatRoomAfterAttendanceCancel(user.getId(), chatRoom.getId());

        if (meeting.isClosed()) {
            meeting.updateStatus(MeetingStatus.OPEN);
            eventPublisherService.publishMeetingStatusChangeEvent(meeting, MeetingStatus.OPEN);
        }

        notificationService.notifyMeetingMember(NotificationType.MEETING_MEMBER_LEFT, meeting, user);

        eventPublisherService.publishMeetingAttendeesChangeEvent(meeting);
    }

    /**
     * 참여한 모임 목록 조회
     */
    @Transactional(readOnly = true)
    public AttendanceGetMeetingListResponse getAttendingMeetingList(AuthUser authUser) {

        User user = userRepository.findActivateUserById(authUser.getId());

        List<MeetingSummaryWithStatusResponse> meetingList = userMeetingRepository.findAllMeetingsByUser(user);

        return new AttendanceGetMeetingListResponse(meetingList);
    }
}
