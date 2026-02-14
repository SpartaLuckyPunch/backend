package com.example.burnchuck.domain.meeting.service;

import static com.example.burnchuck.common.enums.ErrorCode.ACCESS_DENIED;
import static com.example.burnchuck.common.enums.ErrorCode.HOST_NOT_FOUND;
import static com.example.burnchuck.common.enums.ErrorCode.MEETING_NOT_FOUND;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.S3UrlResponse;
import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.ClientInfoExtractor;
import com.example.burnchuck.common.utils.S3UrlGenerator;
import com.example.burnchuck.common.utils.UserDisplay;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.meeting.dto.request.LocationFilterRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingUpdateRequest;
import com.example.burnchuck.domain.meeting.dto.request.UserLocationRequest;
import com.example.burnchuck.domain.meeting.dto.response.AttendeeResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingCreateResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingDetailResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMapPointResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMemberResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingUpdateResponse;
import com.example.burnchuck.domain.meeting.event.MeetingEventPublisher;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import io.lettuce.core.RedisException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final AddressRepository addressRepository;

    private final NotificationService notificationService;
    private final MeetingEventPublisher meetingEventPublisher;
    private final MeetingCacheService meetingCacheService;
    private final ChatRoomService chatRoomService;
    private final S3UrlGenerator s3UrlGenerator;
    private final MeetingSearchService meetingSearchService;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // 서울 광화문 위치
    private final Double DEFAULT_LATITUDE = 37.57;
    private final Double DEFAULT_LONGITUDE = 126.98;

    /**
     * 모임 이미지 업로드 Presigned URL 생성
     */
    public S3UrlResponse getUploadMeetingImgUrl(String filename) {

        String key = "meeting/" + UUID.randomUUID();
        return s3UrlGenerator.generateUploadImgUrl(filename, key);
    }

    /**
     * 모임 생성과 알림 생성 메서드를 호출하는 메서드
     */
    public MeetingCreateResponse createMeetingAndNotify(AuthUser authUser, MeetingCreateRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = createMeeting(user, request);

        notificationService.notifyNewFollowerPost(meeting, user);

        meetingEventPublisher.publishMeetingCreatedEvent(meeting);

        return MeetingCreateResponse.from(meeting);
    }

    /**
     * 모임 생성
     */
    @Transactional
    public Meeting createMeeting(User user, MeetingCreateRequest request) {

        if (!s3UrlGenerator.isFileExists(request.getImgUrl().replaceAll("^https?://[^/]+/", ""))) {
            throw new CustomException(ErrorCode.MEETING_IMG_NOT_FOUND);
        }

        Category category = categoryRepository.findCategoryByCode(request.getCategoryCode());

        Point point = createPoint(request.getLatitude(), request.getLongitude());

        Meeting meeting = Meeting.create(request, category, point);

        meetingRepository.save(meeting);

        chatRoomService.createGroupChatRoom(meeting, user);

        UserMeeting userMeeting = new UserMeeting(
                user,
                meeting,
                MeetingRole.HOST
        );

        userMeetingRepository.save(userMeeting);

        return meeting;
    }

    /**
     * 모임 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<MeetingSummaryResponse> getMeetingPage(
            AuthUser authUser,
            MeetingSearchRequest searchRequest,
            LocationFilterRequest locationRequest,
            UserLocationRequest userLocationRequest,
            MeetingSortOption order,
            Pageable pageable
    ) {
        if (userLocationRequest.noCurrentLocation() && authUser != null) {
            User user = userRepository.findActivateUserWithAddress(authUser.getId());
            userLocationRequest.setLocation(user.getAddress());
        }

        if (userLocationRequest.noCurrentLocation()) {
            userLocationRequest.setLocation(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        }

        if (locationRequest.notNull()) {
            Address address = addressRepository.findAddressByAddressInfo(locationRequest.getProvince(), locationRequest.getCity(), locationRequest.getDistrict());
            userLocationRequest.setLocation(address);
        }

        return meetingSearchService.searchInListFormat(searchRequest, userLocationRequest, order, pageable);
    }

    /**
     * 모임 지도 조회
     */
    @Transactional(readOnly = true)
    public List<MeetingMapPointResponse> getMeetingPointList(
        MeetingSearchRequest searchRequest,
        MeetingMapViewPortRequest viewPort
    ) {
        return meetingSearchService.searchInMapFormat(searchRequest, viewPort);
    }

    /**
     * 모임 단건 요약 조회
     */
    @Transactional(readOnly = true)
    public MeetingSummaryResponse getMeetingSummary(Long meetingId) {

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);
        int currentAttendees = userMeetingRepository.countByMeeting(meeting);

        return MeetingSummaryResponse.from(meeting, currentAttendees);
    }

    /**
     * 모임 단건 조회 및 조회수 관리
     */
    @Transactional(readOnly = true)
    public MeetingDetailResponse getMeetingDetail(Long meetingId, HttpServletRequest httpServletRequest) {

        MeetingDetailResponse meetingDetailResponse = meetingRepository.findMeetingDetail(meetingId)
            .orElseThrow(() -> new CustomException(MEETING_NOT_FOUND));

        String ipAddress = ClientInfoExtractor.extractIpAddress(httpServletRequest);

        try {
            meetingCacheService.increaseViewCount(ipAddress, meetingId);
            Long viewCount = meetingCacheService.getViewCount(meetingId).longValue();

            meetingDetailResponse.increaseViews(viewCount);
        } catch (RedisException | RedisConnectionFailureException e) {
            log.error("Redis 예외 발생: {}", e.getMessage());
        }

        return meetingDetailResponse;
    }

    /**
     * 모임 수정
     */
    @Transactional
    public MeetingUpdateResponse updateMeeting(AuthUser authUser, Long meetingId, MeetingUpdateRequest request) {

        if (!s3UrlGenerator.isFileExists(request.getImgUrl().replaceAll("^https?://[^/]+/", ""))) {
            throw new CustomException(ErrorCode.MEETING_IMG_NOT_FOUND);
        }

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        UserMeeting meetingHost = userMeetingRepository.findHostUserMeetingByMeeting(meeting);
        if (!ObjectUtils.nullSafeEquals(user.getId(), meetingHost.getUser().getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        Category category = categoryRepository.findCategoryByCode(request.getCategoryCode());

        Point point = createPoint(request.getLatitude(), request.getLongitude());

        meeting.updateMeeting(request, category, point);

        meetingEventPublisher.publishMeetingUpdatedEvent(meeting);

        return MeetingUpdateResponse.from(meeting);
    }

    /**
     * 모임 삭제
     */
    @Transactional
    public void deleteMeeting(AuthUser authUser, Long meetingId) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        UserMeeting meetingHost = userMeetingRepository.findHostUserMeetingByMeeting(meeting);
        if (!ObjectUtils.nullSafeEquals(user.getId(), meetingHost.getUser().getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        meeting.delete();

        meetingEventPublisher.publishMeetingDeletedEvent(meeting);
    }

    /**
     * 유저 삭제 후, 해당 유저가 주최한 모임 삭제
     */
    @Transactional
    public void deleteAllHostedMeetingsAfterUserDelete(Long userId) {

        List<Meeting> meetingList = meetingRepository.findActiveHostedMeetings(userId);

        for (Meeting meeting : meetingList) {

            meeting.delete();
            meetingEventPublisher.publishMeetingDeletedEvent(meeting);
        }
    }

    /**
     * 주최한 모임 목록 조회 (로그인한 유저 기준)
     */
    @Transactional(readOnly = true)
    public Page<MeetingSummaryWithStatusResponse> getMyHostedMeetings(AuthUser authUser, Pageable pageable) {

        return meetingRepository.findHostedMeetings(authUser.getId(), pageable);
    }

    /**
     * 주최한 모임 목록 조회 (입력받은 유저 기준)
     */
    @Transactional(readOnly = true)
    public Page<MeetingSummaryWithStatusResponse> getOthersHostedMeetings(Long userId, Pageable pageable) {

        User user = userRepository.findActivateUserById(userId);

        return meetingRepository.findHostedMeetings(user.getId(), pageable);
    }

    /**
     * 모임 참여자 목록 조회
     */
    @Transactional(readOnly = true)
    public MeetingMemberResponse getMeetingMembers(Long meetingId) {

        List<UserMeeting> userMeetings = userMeetingRepository.findMeetingMembers(meetingId);

        if (userMeetings.isEmpty()) {
            throw new CustomException(MEETING_NOT_FOUND);
        }

        UserMeeting host = userMeetings.stream()
            .filter(UserMeeting::isHost)
            .findFirst()
            .orElseThrow(() -> new CustomException(HOST_NOT_FOUND));

        List<AttendeeResponse> attendees = userMeetings.stream()
            .filter(userMeeting -> !userMeeting.isHost())
            .map(userMeeting -> new AttendeeResponse(
                userMeeting.getUser().getId(),
                UserDisplay.resolveProfileImg(userMeeting.getUser()),
                UserDisplay.resolveNickname(userMeeting.getUser())
            ))
            .toList();

        return new MeetingMemberResponse(
            host.getUser().getId(),
            UserDisplay.resolveProfileImg(host.getUser()),
            UserDisplay.resolveNickname(host.getUser()),
            attendees
        );
    }

    /**
     * 위도, 경도 값을 Point 객체로 변환
     */
    private Point createPoint(double latitude, double longitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}

