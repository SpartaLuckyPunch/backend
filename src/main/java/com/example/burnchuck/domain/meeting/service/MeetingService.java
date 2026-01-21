package com.example.burnchuck.domain.meeting.service;

import static com.example.burnchuck.common.enums.ErrorCode.ACCESS_DENIED;
import static com.example.burnchuck.common.enums.ErrorCode.CATEGORY_NOT_FOUND;
import static com.example.burnchuck.common.enums.ErrorCode.MEETING_NOT_FOUND;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.attendance.repository.UserMeetingRepository;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import com.example.burnchuck.domain.meeting.model.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.model.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.model.request.MeetingUpdateRequest;
import com.example.burnchuck.domain.meeting.model.response.HostedMeetingResponse;
import com.example.burnchuck.domain.meeting.model.response.MeetingCreateResponse;
import com.example.burnchuck.domain.meeting.model.response.MeetingDetailResponse;
import com.example.burnchuck.domain.meeting.model.response.MeetingUpdateResponse;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.scheduler.service.EventPublisherService;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final NotificationService notificationService;
    private final EventPublisherService eventPublisherService;

    /**
     * 모임 생성과 알림 생성 메서드를 호출하는 메서드
     */
    public MeetingCreateResponse createMeetingAndNotify(AuthUser authUser, MeetingCreateRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = createMeeting(user, request);

        notificationService.notifyNewFollowerPost(meeting, user);

        eventPublisherService.publishMeetingCreatedEvent(meeting);

        return MeetingCreateResponse.from(meeting);
    }

    /**
     * 모임 생성
     */
    @Transactional
    public Meeting createMeeting(User user, MeetingCreateRequest request) {

        // 1. 카테고리 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        // 2. 모임 생성
        Meeting meeting = new Meeting(request, category);

        // 3. 모임 저장
        meetingRepository.save(meeting);

        // 4. HOST 등록
        UserMeeting userMeeting = new UserMeeting(
                user,
                meeting,
                MeetingRole.HOST
        );

        // 5. HOST 저장
        userMeetingRepository.save(userMeeting);

        return meeting;
    }

    /**
     * 모임 조회
     */
    @Transactional(readOnly = true)
    public Page<MeetingSummaryDto> getMeetingPage(
            String category,
            Pageable pageable
    ) {
        return meetingRepository.findMeetingList(category, pageable);
    }

    /**
     * 모임 단건 조회
     */
    @Transactional
    public MeetingDetailResponse getMeetingDetail(Long meetingId) {

        // 1. 번개 조회
        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        // 2. 번개 조회수 증가
        meeting.increaseViews();

        // 3. QueryDSL에서 응답객체 반환
        return meetingRepository.findMeetingDetail(meetingId)
                .orElseThrow(() -> new CustomException(MEETING_NOT_FOUND));
    }

    /**
     * 모임 수정
     */
    @Transactional
    public MeetingUpdateResponse updateMeeting(AuthUser authUser, Long meetingId, MeetingUpdateRequest request) {

        // 1. 접근 유저 확인
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 모임 확인
        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        // 3. 모임 호스트 확인 및 비교
        UserMeeting meetingHost = userMeetingRepository.findHostByMeeting(meeting);
        if (!Objects.equals(user.getId(), meetingHost.getUser().getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 4. 카테고리 확인
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        // 5. 내용 수정
        meeting.updateMeeting(request, category);

        // 6. 이벤트 생성
        eventPublisherService.publishMeetingUpdatedEvent(meeting);

        // 7. 객체 반환
        return MeetingUpdateResponse.from(meeting);
    }

    /**
     * 모임 삭제
     */
    @Transactional
    public void deleteMeeting(AuthUser authUser, Long meetingId) {

        // 1. 접근 유저 확인
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 모임 조회 (삭제되지 않은 모임)
        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        // 3. HOST 권한 확인
        UserMeeting meetingHost = userMeetingRepository.findHostByMeeting(meeting);
        if (!Objects.equals(user.getId(), meetingHost.getUser().getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 4. 삭제
        meeting.delete();

        // 5. 이벤트 생성
        eventPublisherService.publishMeetingDeletedEvent(meeting);
    }

    /**
     * 주최한 모임 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<HostedMeetingResponse> getHostedMeetings(
            AuthUser authUser,
            Pageable pageable
    ) {
        return meetingRepository.findHostedMeetings(authUser.getId(), pageable);
    }

    /**
     * 모임 검색
     */
    @Transactional(readOnly = true)
    public Page<MeetingSummaryDto> searchMeetings(MeetingSearchRequest request, Pageable pageable) {

        return meetingRepository.searchMeetings(request, pageable);
    }
}

