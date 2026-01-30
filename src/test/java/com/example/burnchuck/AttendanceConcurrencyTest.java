package com.example.burnchuck;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.*;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.enums.UserRole;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.meeting.dto.request.MeetingCreateRequest;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.meeting.service.RedissonLockAttendanceFacade;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AttendanceConcurrencyTest {

    @Autowired private RedissonLockAttendanceFacade redissonLockAttendanceFacade;
    @Autowired private UserMeetingRepository userMeetingRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private CategoryRepository categoryRepository;


    private Meeting meeting;
    private List<AuthUser> authUsers;

    private static final int MAX_ATTENDEES = 10;
    private static final int TOTAL_REQUESTS = 100;

    @BeforeEach
    void setUp() {

        authUsers = new ArrayList<>();

        Address address = addressRepository.save(
                new Address("서울시", "강서구", "발산동", 37.5665, 126.978)
        );

        Category category = categoryRepository.save(
                new Category("테스트 카테고리", "CAT_TEST")
        );

        MeetingCreateRequest meetingRequest = new MeetingCreateRequest(
                "동시성 테스트 모임",
                "100명이 동시에 신청해도 10명만 참여",
                "https://test.img",
                "서울 강서구 발산동",
                37.5665,
                126.9780,
                MAX_ATTENDEES,
                LocalDateTime.now().plusDays(1),
                category.getId()
        );

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = factory.createPoint(
                new Coordinate(126.9780, 37.5665)
        );
        point.setSRID(4326);

        meeting = meetingRepository.save(
                new Meeting(meetingRequest, category, point)
        );

        User host = userRepository.save(
                new User(
                        "host@test.com",
                        "password",
                        "방장",
                        LocalDate.of(1990, 1, 1),
                        false,
                        address,
                        UserRole.USER
                )
        );

        userMeetingRepository.save(
                new UserMeeting(host, meeting, MeetingRole.HOST)
        );

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            User user = userRepository.save(
                    new User(
                            "test" + i + "@test.com",
                            "password",
                            "테스터" + i,
                            LocalDate.of(1995, 1, 1),
                            false,
                            address,
                            UserRole.USER
                    )
            );

            authUsers.add(new AuthUser(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getRole()
            )
            );
        }
        }

    @Test
    @DisplayName("정원 10명인 모임에 100명이 동시에 요청하면 정확히 10명만 참여된다")
    void concurrencyTest() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);

        for (AuthUser authUser : authUsers) {
            executorService.submit(() -> {
                try {
                    redissonLockAttendanceFacade.registerAttendance(authUser, meeting.getId());
                } catch (Exception e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long finalAttendeeCount = userMeetingRepository.countByMeetingId(meeting.getId());
        Meeting updatedMeeting = meetingRepository.findById(meeting.getId()).orElseThrow();

        System.out.println("최종 참여 인원(방장 포함): " + finalAttendeeCount);
        System.out.println("모임 최종 상태: " + updatedMeeting.getStatus());

        // 방장이 1명 미리 있으므로, 일반 유저 9명이 더 들어와서 총 10명이 되어야 함
        assertThat(finalAttendeeCount).isEqualTo(10);
        assertThat(updatedMeeting.getStatus()).isEqualTo(MeetingStatus.CLOSED);
    }
}
