package com.example.burnchuck.domain.attendance.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.attendance.model.response.AttendanceGetMeetingListResponse;
import com.example.burnchuck.domain.attendance.model.response.AttendeeResponse;
import com.example.burnchuck.domain.attendance.model.response.MeetingMemberResponse;
import com.example.burnchuck.domain.attendance.repository.UserMeetingRepository;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.burnchuck.common.enums.ErrorCode.HOST_NOT_FOUND;
import static com.example.burnchuck.common.enums.ErrorCode.MEETING_NOT_FOUND;

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

        // 1. 로그인한 유저 정보로 객체 생성
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 모임 객체 생성
        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        // 3. 모임 상태 확인 (모집 완료 시, 신청 불가)
        if (meeting.getStatus() != MeetingStatus.OPEN) {
            throw new CustomException(ErrorCode.ATTENDANCE_CANNOT_REGISTER);
        }

        // 4. 이미 참가한 모임인지 확인
        boolean exists = userMeetingRepository.existsByUserIdAndMeetingId(user.getId(), meeting.getId());

        if (exists) {
            throw new CustomException(ErrorCode.ATTENDANCE_ALREADY_REGISTERED);
        }

        // 5. UserMeeting 객체 생성 및 저장
        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.PARTICIPANT);

        userMeetingRepository.save(userMeeting);

        // 6. 모임 상태 '모집 마감'으로 변경 (해당 모임의 마지막 참여자인 경우)
        int maxAttendees = meeting.getMaxAttendees();
        int currentAttendees = userMeetingRepository.countByMeeting(meeting);

        if (maxAttendees == currentAttendees) {
            meeting.updateStatus(MeetingStatus.CLOSED);
        }

        // 7. 주최자에게 알림 발송
        notificationService.notifyMeetingMember(true, meeting, user);
    }

    /**
     * 모임 참여 취소
     */
    @Transactional
    public void cancelAttendance(AuthUser authUser, Long meetingId) {

        // 1. 로그인한 유저 정보로 객체 생성
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 모임 객체 생성
        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        // 3. 모임 상태 확인 (COMPLETED 상태인 경우, 취소 불가)
        if (meeting.getStatus() == MeetingStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ATTENDANCE_CANNOT_CANCEL_WHEN_MEETING_CLOSED);
        }

        // 4. UserMeeting 객체 조회
        UserMeeting userMeeting = userMeetingRepository.findUserMeeting(user.getId(), meeting.getId());

        // 5. 주최자인지 확인 -> 주최자는 모임 참여 취소 불가(추후 추가 예정)
        if (userMeeting.getMeetingRole() == MeetingRole.HOST) {
            throw new CustomException(ErrorCode.ATTENDANCE_HOST_CANNOT_CANCEL);
        }

        // 6. 참여 취소(신청 내역 삭제) (추후 채팅방 나가기 처리 추가 예정)
        userMeetingRepository.delete(userMeeting);

        // 7. 모임 상태가 CLOSED인 경우, OPEN으로 변경
        if (meeting.getStatus() == MeetingStatus.CLOSED) {
            meeting.updateStatus(MeetingStatus.OPEN);
        }

        // 8. 주최자에게 알림 발송
        notificationService.notifyMeetingMember(false, meeting, user);
    }

    /**
     * 참여 중인 모임 목록 조회
     */
    @Transactional(readOnly = true)
    public AttendanceGetMeetingListResponse getAttendingMeetingList(AuthUser authUser) {

        // 1. 로그인한 유저 정보로 객체 생성
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. User 기준으로 Meeting 정보 조회
       List<MeetingSummaryDto> meetingList = userMeetingRepository.findAllMeetingsByUser(user);

       return new AttendanceGetMeetingListResponse(meetingList);
    }

    /**
     * 모임 참여자 목록 조회
     */
    @Transactional(readOnly = true)
    public MeetingMemberResponse getMeetingMembers(Long meetingId) {

        // 1. 유저 미팅 객체 조회
        List<UserMeeting> userMeetings = userMeetingRepository.findMeetingMembers(meetingId);

        if (userMeetings.isEmpty()) {
            throw new CustomException(MEETING_NOT_FOUND);
        }

        // 2. 유저 미팅 객체에서 호스트 역할 유저 조회
        UserMeeting host = userMeetings.stream()
                .filter(userMeeting -> userMeeting.getMeetingRole() == MeetingRole.HOST)
                .findFirst()
                .orElseThrow(() -> new CustomException(HOST_NOT_FOUND));

        // 3. 유저 미팅 객체에서 참여자 역할 유저 조회
        List<AttendeeResponse> attendees = userMeetings.stream()
                .filter(userMeeting -> userMeeting.getMeetingRole() == MeetingRole.PARTICIPANT)
                .map(userMeeting -> new AttendeeResponse(
                        userMeeting.getUser().getId(),
                        userMeeting.getUser().getProfileImgUrl(),
                        userMeeting.getUser().getNickname()
                ))
                .toList();

        // 4. 응답 객체 반환
        return new MeetingMemberResponse(
                host.getUser().getId(),
                host.getUser().getProfileImgUrl(),
                host.getUser().getNickname(),
                attendees
        );
    }
}
