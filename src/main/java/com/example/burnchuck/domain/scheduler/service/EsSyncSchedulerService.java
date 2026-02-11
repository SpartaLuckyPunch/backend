package com.example.burnchuck.domain.scheduler.service;

import com.example.burnchuck.domain.meetingLike.repository.MeetingLikeRepository;
import com.example.burnchuck.domain.meetingLike.service.MeetingLikeCacheService;
import java.util.ArrayList;
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

    private final MeetingLikeRepository meetingLikeRepository;
    private final MeetingLikeCacheService meetingLikeCacheService;

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 10분마다 좋아요수 ES에 적용
     */
    @Scheduled(fixedDelay = 600000)
    public void syncLikes() {

        Set<TypedTuple<String>> likeSet = meetingLikeCacheService.getLikeKeyList();

        if (likeSet.isEmpty()) {
            return;
        }

        Set<Long> meetingIdSet = likeSet.stream()
            .map(TypedTuple::getValue)
            .map(Long::valueOf)
            .collect(Collectors.toSet());

        Map<Long, Long> totalLikeMap = meetingLikeRepository.findLikeCountsGroupedByMeetingId(meetingIdSet)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Long) row[1]
            ));

        List<UpdateQuery> updateQueries = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : totalLikeMap.entrySet()) {

            Long meetingId = entry.getKey();
            Long totalLike = entry.getValue();

            Document document = Document.create();
            document.put("likes", totalLike);

            updateQueries.add(
                UpdateQuery.builder(meetingId.toString())
                    .withDocument(document)
                    .build()
            );
        }

        if (!updateQueries.isEmpty()) {
            elasticsearchOperations.bulkUpdate(updateQueries, IndexCoordinates.of("meetings"));
        }

        meetingLikeCacheService.clearLikeKey(meetingIdSet);
    }
}
