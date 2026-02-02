package com.example.burnchuck.domain.meeting.service;

import static com.example.burnchuck.common.enums.ErrorCode.ACCESS_DENIED;
import static com.example.burnchuck.common.enums.ErrorCode.HOST_NOT_FOUND;
import static com.example.burnchuck.common.enums.ErrorCode.MEETING_NOT_FOUND;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.BoundingBox;
import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.MeetingDistance;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.meeting.dto.request.LocationFilterRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapSearchRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingUpdateRequest;
import com.example.burnchuck.domain.meeting.dto.response.AttendeeResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingCreateResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingDetailResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMapPointResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMemberResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryWithStatusResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingUpdateResponse;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.notification.service.NotificationService;
import com.example.burnchuck.domain.scheduler.service.EventPublisherService;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import io.lettuce.core.RedisException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final EventPublisherService eventPublisherService;
    private final MeetingCacheService meetingCacheService;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final ChatRoomService chatRoomService;

    /**
     * 모임 생성과 알림 생성 메서드를 호출하는 메서드
     */
    public MeetingCreateResponse createMeetingAndNotify(AuthUser authUser, MeetingCreateRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = createMeeting(user, request);

        meetingCacheService.saveMeetingLocation(meeting);

        notificationService.notifyNewFollowerPost(meeting, user);

        eventPublisherService.publishMeetingCreatedEvent(meeting);

        return MeetingCreateResponse.from(meeting);
    }

    /**
     * 모임 생성
     */
    @Transactional
    public Meeting createMeeting(User user, MeetingCreateRequest request) {

        Category category = categoryRepository.findCategoryById(request.getCategoryId());

        Point point = createPoint(request.getLatitude(), request.getLongitude());

        Meeting meeting = new Meeting(request, category, point);

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
    public Page<MeetingSummaryResponse> getMeetingPage(
            AuthUser authUser,
            MeetingSearchRequest searchRequest,
            LocationFilterRequest locationRequest,
            Pageable pageable
    ) {
        User user = userRepository.findActivateUserWithAddress(authUser.getId());
        Location location = new Location(user.getAddress().getLatitude(), user.getAddress().getLongitude());

        if (locationRequest.notNull()) {
            Address address = addressRepository.findAddressByAddressInfo(locationRequest.getProvince(), locationRequest.getCity(), locationRequest.getDistrict());
            location = new Location(address.getLatitude(), address.getLongitude());
        }

        List<Long> meetingIdList = null;
        BoundingBox boundingBox = null;

        boolean redisError = false;

        Double radius = searchRequest.getDistance() == null ? 5.0 : searchRequest.getDistance();

        try {
            meetingIdList = meetingCacheService.findMeetingsByLocation(location, radius);
        } catch (RedisException | RedisConnectionFailureException e) {
            boundingBox = MeetingDistance.aroundUserBox(location, radius);
            redisError = true;
        }

        Page<MeetingSummaryResponse> meetingPage = meetingRepository.findMeetingList(searchRequest, pageable, boundingBox, meetingIdList);

        if (redisError && searchRequest.getOrder() == MeetingSortOption.NEAREST) {

            List<MeetingSummaryResponse> meetingSummaryList = new ArrayList<>(meetingPage.getContent());
            sortMeetingsByDistance(meetingSummaryList, location);

            return new PageImpl<>(meetingSummaryList, pageable, meetingPage.getTotalElements());
        }

        return meetingPage;
    }

    /**
     * 모임 지도 조회
     */
    @Transactional(readOnly = true)
    public List<MeetingMapPointResponse> getMeetingPointList(
        MeetingMapSearchRequest searchRequest,
        MeetingMapViewPortRequest viewPort
    ) {
        List<Long> meetingIdList = null;
        BoundingBox boundingBox = null;

        try {
            meetingIdList = meetingCacheService.findMeetingsByViewPort(viewPort);
        } catch (RedisException | RedisConnectionFailureException e) {
            boundingBox = new BoundingBox(viewPort.getMinLat(), viewPort.getMaxLat(), viewPort.getMinLng(), viewPort.getMaxLng());
        }

        return meetingRepository.findMeetingPointList(searchRequest, boundingBox, meetingIdList);
    }

    /**
     * 모임 단건 요약 조회
     */
    @Transactional
    public MeetingSummaryResponse getMeetingSummary(Long meetingId) {

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);
        int currentAttendees = userMeetingRepository.countByMeeting(meeting);

        return MeetingSummaryResponse.from(meeting, currentAttendees);
    }

    /**
     * 모임 단건 조회
     */
    @Transactional
    public MeetingDetailResponse getMeetingDetail(Long meetingId) {

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        meeting.increaseViews();

        return meetingRepository.findMeetingDetail(meetingId)
                .orElseThrow(() -> new CustomException(MEETING_NOT_FOUND));
    }

    /**
     * 모임 수정
     */
    @Transactional
    public MeetingUpdateResponse updateMeeting(AuthUser authUser, Long meetingId, MeetingUpdateRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        UserMeeting meetingHost = userMeetingRepository.findHostByMeeting(meeting);
        if (!ObjectUtils.nullSafeEquals(user.getId(), meetingHost.getUser().getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        Category category = categoryRepository.findCategoryById(request.getCategoryId());

        Point point = createPoint(request.getLatitude(), request.getLongitude());

        meeting.updateMeeting(request, category, point);

        meetingCacheService.saveMeetingLocation(meeting);

        eventPublisherService.publishMeetingUpdatedEvent(meeting);

        return MeetingUpdateResponse.from(meeting);
    }

    /**
     * 모임 삭제
     */
    @Transactional
    public void deleteMeeting(AuthUser authUser, Long meetingId) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Meeting meeting = meetingRepository.findActivateMeetingById(meetingId);

        UserMeeting meetingHost = userMeetingRepository.findHostByMeeting(meeting);
        if (!ObjectUtils.nullSafeEquals(user.getId(), meetingHost.getUser().getId())) {
            throw new CustomException(ACCESS_DENIED);
        }

        meeting.delete();

        meetingCacheService.deleteMeetingLocation(meeting.getId());

        eventPublisherService.publishMeetingDeletedEvent(meeting);
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
            .filter(userMeeting -> userMeeting.isHost())
            .findFirst()
            .orElseThrow(() -> new CustomException(HOST_NOT_FOUND));

        List<AttendeeResponse> attendees = userMeetings.stream()
            .filter(userMeeting -> !userMeeting.isHost())
            .map(userMeeting -> new AttendeeResponse(
                userMeeting.getUser().getId(),
                userMeeting.getUser().getProfileImgUrl(),
                userMeeting.getUser().getNickname()
            ))
            .toList();

        return new MeetingMemberResponse(
            host.getUser().getId(),
            host.getUser().getProfileImgUrl(),
            host.getUser().getNickname(),
            attendees
        );
    }

    /**
     * 중심지 기준 가까운순 정렬
     */
    private void sortMeetingsByDistance(List<MeetingSummaryResponse> meetings, Location location) {

        meetings.sort(Comparator.comparingDouble(
            m -> MeetingDistance.calculateDistance(location, m)
        ));
    }

    /**
     * 위도, 경도 값을 Point 객체로 변환
     */
    private Point createPoint(double latitude, double longitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}

