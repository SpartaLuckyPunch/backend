package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meeting.model.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.model.response.MeetingCreateResponse;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 모임 생성
     */
    @Transactional
    public MeetingCreateResponse createMeeting(AuthUser user, MeetingCreateRequest request) {

        // 1. Token을 통해 Meeting Host 유저 조회
        User host = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // Request DTO에서 String으로 입력받은 카테고리가 존재하는지 확인? (카테고리 도메인 이전이므로 생략)

        // 2. Request DTO -> Entity에 매칭하기
        Meeting meeting = new Meeting(
                request.getTitle(),
                request.getDescription(),
                request.getImgUrl(),
                request.getLocation(),
                request.getMaxAttendees(),
                request.getMeetingDateTime(),
                request.getCategory()
        );

        meetingRepository.save(meeting);

        return MeetingCreateResponse.from(meeting);
    }
}
