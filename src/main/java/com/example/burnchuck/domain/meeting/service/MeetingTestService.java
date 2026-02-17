package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.dto.CustomBoundingBox;
import com.example.burnchuck.common.dto.Location;
import com.example.burnchuck.common.dto.PageResponse;
import com.example.burnchuck.common.enums.MeetingSortOption;
import com.example.burnchuck.common.utils.MeetingDistance;
import com.example.burnchuck.domain.meeting.dto.request.MeetingMapViewPortRequest;
import com.example.burnchuck.domain.meeting.dto.request.MeetingSearchRequest;
import com.example.burnchuck.domain.meeting.dto.request.UserLocationRequest;
import com.example.burnchuck.domain.meeting.dto.response.MeetingMapPointResponse;
import com.example.burnchuck.domain.meeting.dto.response.MeetingSummaryResponse;
import com.example.burnchuck.domain.meeting.repository.MeetingSearchTempRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingTestService {

    private final MeetingCacheTest meetingCacheTest;
    private final MeetingSearchTempRepository meetingSearchTemp;
    private final MeetingSearchService meetingSearchService;

    /**
     * DB 조회
     */
    @Transactional(readOnly = true)
    public Page<MeetingSummaryResponse> dbSearchList(
        MeetingSearchRequest searchRequest,
        UserLocationRequest userLocationRequest,
        MeetingSortOption order,
        Pageable pageable
    ) {
        Location location = new Location(userLocationRequest.getLatitude(), userLocationRequest.getLongitude());

        Double radius = userLocationRequest.getDistance() == null ? 5.0 : userLocationRequest.getDistance();

        CustomBoundingBox boundingBox = MeetingDistance.aroundUserBox(location, radius);

        Page<MeetingSummaryResponse> meetingPage = meetingSearchTemp.findMeetingList(searchRequest, order, pageable, boundingBox, null);

        if (order == MeetingSortOption.NEAREST) {

            List<MeetingSummaryResponse> meetingSummaryList = new ArrayList<>(meetingPage.getContent());
            sortMeetingsByDistance(meetingSummaryList, location);

            return new PageImpl<>(meetingSummaryList, pageable, meetingPage.getTotalElements());
        }

        return meetingPage;
    }

    private void sortMeetingsByDistance(List<MeetingSummaryResponse> meetings, Location location) {

        meetings.sort(Comparator.comparingDouble(
            m -> MeetingDistance.calculateDistance(location, m)
        ));
    }

    @Transactional(readOnly = true)
    public List<MeetingMapPointResponse> dbSearchMap(
        MeetingSearchRequest searchRequest,
        MeetingMapViewPortRequest viewPort
    ) {
        CustomBoundingBox boundingBox = new CustomBoundingBox(viewPort.getMinLat(), viewPort.getMaxLat(), viewPort.getMinLng(), viewPort.getMaxLng());
        return meetingSearchTemp.findMeetingPointList(searchRequest, boundingBox, null);
    }

    /**
     * Redis 조회
     */
    @Transactional(readOnly = true)
    public Page<MeetingSummaryResponse> redisSearchList(
        MeetingSearchRequest searchRequest,
        UserLocationRequest userLocationRequest,
        MeetingSortOption order,
        Pageable pageable
    ) {
        Location location = new Location(userLocationRequest.getLatitude(), userLocationRequest.getLongitude());

        Double radius = userLocationRequest.getDistance() == null ? 5.0 : userLocationRequest.getDistance();

        List<Long> meetingIdList = meetingCacheTest.findMeetingsByLocation(location, radius);

        Page<MeetingSummaryResponse> meetingPage = meetingSearchTemp.findMeetingList(searchRequest, order, pageable, null, meetingIdList);

        if (order == MeetingSortOption.NEAREST) {

            List<MeetingSummaryResponse> meetingSummaryList = new ArrayList<>(meetingPage.getContent());
            sortMeetingsByDistance(meetingSummaryList, location);

            return new PageImpl<>(meetingSummaryList, pageable, meetingPage.getTotalElements());
        }

        return meetingPage;
    }

    @Transactional(readOnly = true)
    public List<MeetingMapPointResponse> redisSearchMap(
        MeetingSearchRequest searchRequest,
        MeetingMapViewPortRequest viewPort
    ) {
        List<Long> meetingIdList = meetingCacheTest.findMeetingsByViewPort(viewPort);

        return meetingSearchTemp.findMeetingPointList(searchRequest, null, meetingIdList);
    }

    @Transactional(readOnly = true)
    public PageResponse<MeetingSummaryResponse> esSearchList(
        MeetingSearchRequest searchRequest,
        UserLocationRequest userLocationRequest,
        MeetingSortOption order,
        Pageable pageable
    ) {
        return meetingSearchService.searchInListFormat(searchRequest, userLocationRequest, order, pageable);
    }

    /**
     * 모임 지도 조회
     */
    @Transactional(readOnly = true)
    public List<MeetingMapPointResponse> esSearchMap(
        MeetingSearchRequest searchRequest,
        MeetingMapViewPortRequest viewPort
    ) {
        return meetingSearchService.searchInMapFormat(searchRequest, viewPort);
    }
}
