package com.example.burnchuck.domain.attendance.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.attendance.model.response.AttendanceGetMeetingListResponse;
import com.example.burnchuck.domain.attendance.repository.UserMeetingRepository;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final UserMeetingRepository userMeetingRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    /**
     * 모임 참여 신청
     */
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
    }

    /**
     * 모임 참여 취소
     */
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
    }

    /**
     * 참여 중인 모임 목록 조회
     */
    public AttendanceGetMeetingListResponse getAttendingMeetingList(AuthUser authUser) {

        // 1. 로그인한 유저 정보로 객체 생성
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. User 기준으로 Meeting 정보 조회
       List<MeetingSummaryDto> meetingList = userMeetingRepository.findAllMeetingsByUser(user);

       return new AttendanceGetMeetingListResponse(meetingList);
    }
}
