package com.example.burnchuck.domain.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.ClientInfoExtractor;
import com.example.burnchuck.common.utils.S3UrlGenerator;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.meeting.dto.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingDetailResponse;
import com.example.burnchuck.domain.meeting.event.MeetingEventPublisher;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import com.example.burnchuck.fixture.MeetingFixture;
import com.example.burnchuck.fixture.UserFixture;
import io.lettuce.core.RedisException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserMeetingRepository userMeetingRepository;
    @Mock
    private MeetingEventPublisher meetingEventPublisher;
    @Mock
    private MeetingCacheService meetingCacheService;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private S3UrlGenerator s3UrlGenerator;
    @Mock
    private HttpServletRequest httpServletRequest;

    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @InjectMocks
    private MeetingService meetingService;

    User user;
    AuthUser authUser;

    Category category = MeetingFixture.category;

    @BeforeEach
    void setUp() {
        user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        authUser = new AuthUser(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }


    @Test
    @DisplayName("모임 생성 성공")
    void createMeeting_success() {

        // Given
        MeetingCreateRequest request = new MeetingCreateRequest(
            "테스트",
            "테스트용 모임 생성",
            "www.test.com/test.png",
            "서울시 강동구 천호동",
            37.5450159,
            127.1368066,
            5,
            LocalDateTime.now().plusDays(3),
            "testcategory"
        );

        Meeting meeting = MeetingFixture.testMeeting();
        ReflectionTestUtils.setField(meeting, "id", 1L);

        Point point = geometryFactory.createPoint(new Coordinate(37.5450159, 127.1368066));
        point.setSRID(4326);

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(s3UrlGenerator.isFileExists(anyString())).thenReturn(true);

        // When
        meetingService.createMeeting(authUser, request);

        // Then
        verify(meetingRepository).save(any(Meeting.class));
        verify(chatRoomService).createGroupChatRoom(any(Meeting.class), any(User.class));
        verify(userMeetingRepository).save(any(UserMeeting.class));
        verify(meetingEventPublisher).publishMeetingCreatedEvent(any(Meeting.class));
    }

    @Test
    @DisplayName("모임 생성 실패 - 이미지 X")
    void createMeeting_failure_notFoundImage() {

        // Given
        MeetingCreateRequest request = new MeetingCreateRequest(
            "테스트",
            "테스트용 모임 생성",
            "www.test.com/test.png",
            "서울시 강동구 천호동",
            37.5450159,
            127.1368066,
            5,
            LocalDateTime.now().plusDays(3),
            "testcategory"
        );

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(s3UrlGenerator.isFileExists(anyString())).thenReturn(false);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> meetingService.createMeeting(authUser, request));

        assertEquals("모임 이미지가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("모임 단건 조회 성공")
    void getMeetingDetail_success() {

        // Given
        Long meetingId = 1L;
        MeetingDetailResponse meetingDetailResponse = new MeetingDetailResponse(
            meetingId, "테스트", "www.test.com/test.png", "테스트용 모임 생성",
            "서울시 강동구 천호동", 37.5450159, 127.1368066,
            LocalDateTime.now().plusDays(3), 5, 1, "OPEN", 0L, 0L
        );

        String ipAddress = "ipAddress";

        when(meetingRepository.findMeetingDetail(anyLong())).thenReturn(Optional.of(meetingDetailResponse));
        when(meetingCacheService.getViewCount(anyLong())).thenReturn(10.0);
        when(ClientInfoExtractor.extractIpAddress(httpServletRequest)).thenReturn(ipAddress);

        // When
        MeetingDetailResponse response = meetingService.getMeetingDetail(meetingId, httpServletRequest);

        // Then
        assertThat(response.getViews()).isEqualTo(10L);
        verify(meetingCacheService).increaseViewCount(anyString(), anyLong());
    }

    @Test
    @DisplayName("모임 단건 조회 성공 - Redis 예외 발생 시 조회수 미반영")
    void getMeetingDetail_success_redisException() {

        // Given
        Long meetingId = 1L;
        MeetingDetailResponse meetingDetailResponse = new MeetingDetailResponse(
            meetingId, "테스트", "www.test.com/test.png", "테스트용 모임 생성",
            "서울시 강동구 천호동", 37.5450159, 127.1368066,
            LocalDateTime.now().plusDays(3), 5, 1, "OPEN", 0L, 0L
        );

        String ipAddress = "ipAddress";

        when(meetingRepository.findMeetingDetail(anyLong())).thenReturn(Optional.of(meetingDetailResponse));
        doThrow(new RedisException("connection failed")).when(meetingCacheService).increaseViewCount(any(), anyLong());
        when(ClientInfoExtractor.extractIpAddress(httpServletRequest)).thenReturn(ipAddress);

        // When
        MeetingDetailResponse response = meetingService.getMeetingDetail(meetingId, httpServletRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getViews()).isEqualTo(0L);
    }

    @Test
    @DisplayName("모임 삭제 성공")
    void deleteMeeting_success() {

        // Given
        Meeting meeting = MeetingFixture.testMeeting();
        ReflectionTestUtils.setField(meeting, "id", 1L);

        UserMeeting userMeeting = new UserMeeting(user, meeting, MeetingRole.HOST);
        ReflectionTestUtils.setField(userMeeting, "id", 1L);

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.findHostUserMeetingByMeeting(any(Meeting.class))).thenReturn(userMeeting);

        // When
        meetingService.deleteMeeting(authUser, meeting.getId());

        // Then
        assertThat(meeting.isDeleted()).isEqualTo(true);
        verify(meetingEventPublisher).publishMeetingDeletedEvent(any(Meeting.class));
    }

    @Test
    @DisplayName("모임 삭제 실패 - host 아닌 사용자 접근")
    void deleteMeeting_failure_notHost() {

        // Given
        User hostUser = UserFixture.testUser2();
        ReflectionTestUtils.setField(hostUser, "id", 2L);

        Meeting meeting = MeetingFixture.testMeeting();
        ReflectionTestUtils.setField(meeting, "id", 1L);

        UserMeeting userMeeting = new UserMeeting(hostUser, meeting, MeetingRole.HOST);
        ReflectionTestUtils.setField(userMeeting, "id", 1L);

        when(userRepository.findActivateUserById(anyLong())).thenReturn(user);
        when(meetingRepository.findActivateMeetingById(anyLong())).thenReturn(meeting);
        when(userMeetingRepository.findHostUserMeetingByMeeting(any(Meeting.class))).thenReturn(userMeeting);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> meetingService.deleteMeeting(authUser, meeting.getId()));

        assertEquals("접근 권한이 없습니다.", exception.getMessage());
    }
}