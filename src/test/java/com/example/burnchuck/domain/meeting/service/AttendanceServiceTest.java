package com.example.burnchuck.domain.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.enums.NotificationType;
import com.example.burnchuck.common.enums.RoomType;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.chat.repository.ChatRoomRepository;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.meeting.event.MeetingEventPublisher;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import com.example.burnchuck.fixture.MeetingFixture;
import com.example.burnchuck.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private UserMeetingRepository userMeetingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private MeetingEventPublisher meetingEventPublisher;

    @InjectMocks
    private AttendanceService attendanceService;

    User user;
    AuthUser authUser;
    Meeting meeting;

    @BeforeEach
    void setUp() {
        user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        authUser = new AuthUser(user.getId(), user.getEmail(), user.getNickname(), user.getRole());

        meeting = MeetingFixture.testMeeting();
        ReflectionTestUtils.setField(meeting, "id", 1L);
    }

    @Test
    @DisplayName("정상 참여 신청")
    void registerAttendance_success() {

        // Given
        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.PARTICIPANT);

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.existsByUserIdAndMeetingId(anyLong(), anyLong())).thenReturn(false);
        when(userMeetingRepository.countByMeeting(any(Meeting.class))).thenReturn(1);
        when(userMeetingRepository.save(any(UserMeeting.class))).thenReturn(userMeeting);

        // When
        attendanceService.registerAttendance(authUser, meeting.getId());

        // Then
        verify(userMeetingRepository).save(any(UserMeeting.class));
        verify(chatRoomService).joinGroupChatRoom(meeting.getId(), user);
        verify(meetingEventPublisher).publishMeetingAttendeesChangeEvent(NotificationType.MEETING_MEMBER_JOIN, meeting, user);
    }

    @Test
    @DisplayName("정상 참여 신청 - 모임 상태 Closed로 변경")
    void registerAttendance_success_changeStatus() {

        // Given
        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.PARTICIPANT);

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.existsByUserIdAndMeetingId(anyLong(), anyLong())).thenReturn(false);
        when(userMeetingRepository.countByMeeting(any(Meeting.class))).thenReturn(4);
        when(userMeetingRepository.save(any(UserMeeting.class))).thenReturn(userMeeting);

        // When
        attendanceService.registerAttendance(authUser, meeting.getId());

        // Then
        assertThat(meeting.getStatus()).isEqualTo(MeetingStatus.CLOSED);
    }

    @Test
    @DisplayName("참여 신청 실패 - 모임 상태가 OPEN이 아닐 때")
    void registerAttendance_failure_notOpen() {

        // Given
        ReflectionTestUtils.setField(meeting, "status", MeetingStatus.CLOSED);

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> attendanceService.registerAttendance(authUser, meeting.getId()));

        assertEquals("모집 완료된 번개입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 실패 - 이미 참여한 모임")
    void registerAttendance_failure_alreadyRegistered() {

        // Given
        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.existsByUserIdAndMeetingId(anyLong(), anyLong())).thenReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> attendanceService.registerAttendance(authUser, meeting.getId()));

        assertEquals("이미 참여 신청한 번개입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("참여 신청 실패 - 정원 초과")
    void registerAttendance_failure_moreThanMax() {

        // Given
        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.existsByUserIdAndMeetingId(anyLong(), anyLong())).thenReturn(false);
        when(userMeetingRepository.countByMeeting(any(Meeting.class))).thenReturn(5);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> attendanceService.registerAttendance(authUser, meeting.getId()));

        assertEquals("모임의 정원이 모두 찼습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("정상 취소")
    void cancelAttendance_success() {

        // Given
        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.PARTICIPANT);

        ChatRoom chatRoom = new ChatRoom(meeting.getTitle(), RoomType.GROUP, meeting.getId());

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.findUserMeeting(anyLong(), anyLong())).thenReturn(userMeeting);
        when(chatRoomRepository.findChatRoomByMeetingId(anyLong())).thenReturn(chatRoom);

        // When
        attendanceService.cancelAttendance(authUser, meeting.getId());

        // Then
        verify(userMeetingRepository).delete(userMeeting);
        verify(chatRoomService).leaveChatRoomRegardlessOfStatus(user.getId(), chatRoom.getId());
        verify(meetingEventPublisher).publishMeetingAttendeesChangeEvent(NotificationType.MEETING_MEMBER_LEFT, meeting, user);
    }

    @Test
    @DisplayName("정상 취소 - 취소 후 상태 변경")
    void cancelAttendance_success_changeStatus() {

        // Given
        ReflectionTestUtils.setField(meeting, "status", MeetingStatus.CLOSED);

        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.PARTICIPANT);

        ChatRoom chatRoom = new ChatRoom(meeting.getTitle(), RoomType.GROUP, meeting.getId());

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.findUserMeeting(anyLong(), anyLong())).thenReturn(userMeeting);
        when(chatRoomRepository.findChatRoomByMeetingId(anyLong())).thenReturn(chatRoom);

        // When
        attendanceService.cancelAttendance(authUser, meeting.getId());

        // Then
        verify(userMeetingRepository).delete(userMeeting);
        verify(chatRoomService).leaveChatRoomRegardlessOfStatus(user.getId(), chatRoom.getId());
        verify(meetingEventPublisher).publishMeetingStatusChangeEvent(meeting, MeetingStatus.OPEN);
        verify(meetingEventPublisher).publishMeetingAttendeesChangeEvent(NotificationType.MEETING_MEMBER_LEFT, meeting, user);

        assertThat(meeting.getStatus()).isEqualTo(MeetingStatus.OPEN);
    }

    @Test
    @DisplayName("취소 실패 - 모임 상태 COMPLETED")
    void cancelAttendance_failure_completedMeeting() {

        // Given
        ReflectionTestUtils.setField(meeting, "status", MeetingStatus.COMPLETED);

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> attendanceService.cancelAttendance(authUser, meeting.getId()));

        assertEquals("번개 시작 10분 전에는 취소할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("취소 실패 - Host가 취소 시도")
    void cancelAttendance_failure_host() {

        // Given
        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.HOST);

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.findUserMeeting(anyLong(), anyLong())).thenReturn(userMeeting);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> attendanceService.cancelAttendance(authUser, meeting.getId()));

        assertEquals("호스트는 번개 참여를 취소할 수 없습니다.", exception.getMessage());
    }
}