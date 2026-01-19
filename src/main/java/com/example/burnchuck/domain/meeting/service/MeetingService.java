package com.example.burnchuck.domain.meeting.service;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.attendance.repository.UserMeetingRepository;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.meeting.model.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.model.response.MeetingCreateResponse;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.burnchuck.common.enums.ErrorCode.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserMeetingRepository userMeetingRepository;

    /**
     * 모임 생성
     */
    @Transactional
    public MeetingCreateResponse createMeeting(AuthUser authUser, MeetingCreateRequest request) {

        // 1. 유저 조회
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 카테고리 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        // 3. 모임 생성
        Meeting meeting = new Meeting(
                request.getTitle(),
                request.getDescription(),
                request.getImgUrl(),
                request.getLocation(),
                request.getLatitude(),
                request.getLongitude(),
                request.getMaxAttendees(),
                request.getMeetingDateTime(),
                MeetingStatus.OPEN,
                category
        );

        // 4. 모임 저장
        meetingRepository.save(meeting);

        // 5. HOST 등록
        UserMeeting userMeeting = new UserMeeting(
                user,
                meeting,
                MeetingRole.HOST
        );

        // 6. HOST 저장
        userMeetingRepository.save(userMeeting);

        return MeetingCreateResponse.from(meeting);
    }
}
