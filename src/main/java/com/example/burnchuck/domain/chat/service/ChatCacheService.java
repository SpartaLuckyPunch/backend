package com.example.burnchuck.domain.chat.service;

import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import io.lettuce.core.RedisException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ChatRedisCache")
public class ChatCacheService {

    private final StringRedisTemplate redisTemplate;

    private static final String ROOM_SEQ_KEY = "chat:room:%s:seq"; // 방의 현재 메시지 시퀀스
    private static final String USER_READ_KEY = "chat:room:%s:user:%s:read"; // 유저가 마지막으로 읽은 시퀀스
    private static final long CHAT_CACHE_TTL = 30; // 30일

    /**
     * 메시지 시퀀스 생성
     */
    public Long generateMessageSequence(Long roomId) {
        String key = String.format(ROOM_SEQ_KEY, roomId);
        try {
            Long sequence = redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, CHAT_CACHE_TTL, TimeUnit.DAYS);
            return sequence;
        } catch (RedisException | RedisConnectionFailureException e) {
            log.error("Redis 시퀀스 생성 실패: {}", e.getMessage());
            return 0L;
        } catch (Exception e) {
            log.error("error : {}", e.getMessage());
            throw new CustomException(ErrorCode.CHAT_MESSAGE_SEND_FAILED);
        }
    }

    /**
     * 유저의 마지막 읽은 위치 업데이트
     */
    public void updateLastReadSequence(Long roomId, Long userId, Long lastReadSeq) {
        String key = String.format(USER_READ_KEY, roomId, userId);
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(lastReadSeq));
            redisTemplate.expire(key, CHAT_CACHE_TTL, TimeUnit.DAYS);
        } catch (RedisException | RedisConnectionFailureException e) {
            log.error("Redis 읽음 처리 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("error : {}", e.getMessage());
            throw new CustomException(ErrorCode.LAST_READ_UPDATE_FAILED);
        }
    }

    /**
     * 현재 방의 마지막 시퀀스 조회
     */
    public Long getRoomCurrentSequence(Long roomId) {
        String key = String.format(ROOM_SEQ_KEY, roomId);
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0L : Long.parseLong(value);
    }

    /**
     * 여러 채팅방의 안 읽은 메시지 수를 한 번에 조회 (Batch)
     */
    public Map<Long, Long> getUnreadCountsBatch(Long userId, List<Long> roomIds) {

        if (roomIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long roomId : roomIds) {
                String roomKey = String.format(ROOM_SEQ_KEY, roomId);
                connection.stringCommands().get(roomKey.getBytes(StandardCharsets.UTF_8));

                String userKey = String.format(USER_READ_KEY, roomId, userId);
                connection.stringCommands().get(userKey.getBytes(StandardCharsets.UTF_8));
            }
            return null;
        });

        Map<Long, Long> unreadCounts = new HashMap<>();

        for (int i = 0; i < roomIds.size(); i++) {
            Long roomId = roomIds.get(i);

            // RedisCallback 결과는 byte[]로 올 수 있으므로 안전하게 파싱
            Long roomSeq = parseLongFromRedisResult(results.get(i * 2));
            Long userSeq = parseLongFromRedisResult(results.get(i * 2 + 1));

            long unread = roomSeq - userSeq;
            if (unread < 0) unread = 0;

            unreadCounts.put(roomId, unread);
        }

        return unreadCounts;
    }

    /**
     * 특정 메시지를 안 읽은 사람 수 계산을 위해 멤버들의 읽은 위치 조회
     */
    public Map<Long, Long> getMembersLastReadSequence(Long roomId, List<Long> memberIds) {

        if (memberIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long memberId : memberIds) {
                String key = String.format(USER_READ_KEY, roomId, memberId);
                connection.stringCommands().get(key.getBytes(StandardCharsets.UTF_8));
            }
            return null;
        });

        Map<Long, Long> memberReadMap = new HashMap<>();
        for (int i = 0; i < memberIds.size(); i++) {
            Long memberId = memberIds.get(i);
            Long readSeq = parseLongFromRedisResult(results.get(i));
            memberReadMap.put(memberId, readSeq);
        }
        return memberReadMap;
    }

    /**
     * Redis Pipeline 결과를 Long으로 변환하는 메서드
     */
    private Long parseLongFromRedisResult(Object result) {

        if (result == null) {
            return 0L;
        }

        try {
            if (result instanceof byte[]) {
                String str = new String((byte[]) result, StandardCharsets.UTF_8);
                return Long.parseLong(str);
            }
            if (result instanceof String) {
                return Long.parseLong((String) result);
            }
            if (result instanceof Long) {
                return (Long) result;
            }
            return 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * 유저가 마지막 조회 시퀀스 삭제
     */
    public void deleteUserReadInfo(Long roomId, Long userId) {
        String key = String.format(USER_READ_KEY, roomId, userId);
        try {
            redisTemplate.delete(key);
        } catch (RedisException | RedisConnectionFailureException e) {
            log.error("Redis 키 삭제 실패: {}", e.getMessage());
        }
    }
}