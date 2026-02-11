package com.example.burnchuck.domain.scheduler.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.service.MeetingCacheService;
import com.example.burnchuck.domain.meetingLike.repository.MeetingLikeRepository;
import com.example.burnchuck.domain.meetingLike.service.MeetingLikeCacheService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EsSyncSchedulerService {

    private final MeetingRepository meetingRepository;
    private final MeetingLikeRepository meetingLikeRepository;
    private final MeetingCacheService meetingCacheService;
    private final MeetingLikeCacheService meetingLikeCacheService;

    private final ElasticsearchOperations elasticsearchOperations;

    private static int LIKE_WEIGHT = 5;

    /**
     * 30분마다 조회수, 좋아요수 ES에 적용
     */
//    @Scheduled(fixedDelay = 1800000)
    @Scheduled(fixedDelay = 180000)
    public void syncPopularityScore() {

        Set<Long> likeMeetingIdList = meetingLikeCacheService.getLikeKeyList();
        Set<TypedTuple<String>> viewMeetingIdAndCountList = meetingCacheService.getAllViewList(LocalDate.now());

        if (likeMeetingIdList.isEmpty() && viewMeetingIdAndCountList.isEmpty()) {
            return;
        }

        Map<Long, Double> todayViewMap = viewMeetingIdAndCountList.stream()
            .collect(Collectors.toMap(
                tuple -> Long.valueOf(tuple.getValue()),
                TypedTuple::getScore
            ));

        Set<Long> viewMeetingIdList = todayViewMap.keySet();

        Set<Long> targetMeetingIds = new HashSet<>(viewMeetingIdList);
        targetMeetingIds.addAll(likeMeetingIdList);

        if (targetMeetingIds.isEmpty()) {
            return;
        }

        Map<Long, Long> totalLikeMap = meetingLikeRepository.findByMeetingIdIn(targetMeetingIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Long) row[1]
            ));

        List<Meeting> meetingList = meetingRepository.findAllById(targetMeetingIds);

        List<UpdateQuery> updateQueries = new ArrayList<>();

        for (Meeting meeting : meetingList) {

            Long meetingId = meeting.getId();

            long totalView = meeting.getViews();
            long todayView = todayViewMap.getOrDefault(meetingId, 0.0).longValue();
            long totalLike = totalLikeMap.getOrDefault(meetingId, 0L);

            long popularityScore = (totalView + todayView) + totalLike * LIKE_WEIGHT;

            Document document = Document.create();
            document.put("popularityScore", popularityScore);

            updateQueries.add(
                UpdateQuery.builder(meetingId.toString())
                    .withDocument(document)
                    .build()
            );
        }

        if (!updateQueries.isEmpty()) {
            elasticsearchOperations.bulkUpdate(updateQueries, IndexCoordinates.of("meetings"));
        }

        meetingLikeCacheService.clearLikeKey(viewMeetingIdList);
    }
}
