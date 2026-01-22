package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.domain.meeting.dto.response.AttendanceGetMeetingListResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
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
    private final NotificationService notificationService;

    /**
     * 모임 참여 신청
     */
    @Transactional
    public void registerAttendance(AuthUser authUser, Long meetingId) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        if (meeting.getStatus() != MeetingStatus.OPEN) {
            throw new CustomException(ErrorCode.ATTENDANCE_CANNOT_REGISTER);
        }

        boolean exists = userMeetingRepository.existsByUserIdAndMeetingId(user.getId(), meeting.getId());

        if (exists) {
            throw new CustomException(ErrorCode.ATTENDANCE_ALREADY_REGISTERED);
        }

        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.PARTICIPANT);

        userMeetingRepository.save(userMeeting);

        int maxAttendees = meeting.getMaxAttendees();
        int currentAttendees = userMeetingRepository.countByMeeting(meeting);

        if (maxAttendees == currentAttendees) {
            meeting.updateStatus(MeetingStatus.CLOSED);
        }

        notificationService.notifyMeetingMember(true, meeting, user);
    }

    /**
     * 모임 참여 취소
     */
    @Transactional
    public void cancelAttendance(AuthUser authUser, Long meetingId) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        if (meeting.getStatus() == MeetingStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ATTENDANCE_CANNOT_CANCEL_WHEN_MEETING_CLOSED);
        }

        UserMeeting userMeeting = userMeetingRepository.findUserMeeting(user.getId(), meeting.getId());

        if (userMeeting.getMeetingRole() == MeetingRole.HOST) {
            throw new CustomException(ErrorCode.ATTENDANCE_HOST_CANNOT_CANCEL);
        }

        userMeetingRepository.delete(userMeeting);

        if (meeting.getStatus() == MeetingStatus.CLOSED) {
            meeting.updateStatus(MeetingStatus.OPEN);
        }

        notificationService.notifyMeetingMember(false, meeting, user);
    }

    /**
     * 참여 중인 모임 목록 조회
     */
    @Transactional(readOnly = true)
    public AttendanceGetMeetingListResponse getAttendingMeetingList(AuthUser authUser) {

        User user = userRepository.findActivateUserById(authUser.getId());

        List<MeetingSummaryWithStatusResponse> meetingList = userMeetingRepository.findAllMeetingsByUser(user);

        return new AttendanceGetMeetingListResponse(meetingList);
    }
}
