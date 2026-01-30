package com.example.burnchuck;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.common.enums.MeetingRole;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.enums.UserRole;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.meeting.service.AttendanceFacade;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AttendanceConcurrencyTest {

    @Autowired private AttendanceFacade attendanceFacade;
    @Autowired private UserMeetingRepository userMeetingRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE review_reaction");
        jdbcTemplate.execute("TRUNCATE TABLE reviews");
        jdbcTemplate.execute("TRUNCATE TABLE user_meeting");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    @Test
    @DisplayName("정원 10명인 모임에 100명이 동시에 요청하면 정확히 10명만 참여된다")
    void concurrencyTest() throws InterruptedException {

        // 2. 기초 데이터 설정
        Long meetingId = 1L;

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("ID 1인 Meeting이 필요"));

        meeting.updateStatus(MeetingStatus.OPEN);
        meeting.updateMaxAttendees(10);
        meetingRepository.saveAndFlush(meeting);

        Address address = addressRepository.saveAndFlush(
                new Address("서울시",
                        "강서구",
                        "발산동",
                        37.5665,
                        126.978));

        User host = userRepository.saveAndFlush(new User(
                "host@example.com",
                "pass!",
                "방장",
                LocalDate.of(1990, 1, 1),
                false,
                address,
                UserRole.USER
        ));

        userMeetingRepository.saveAndFlush(new UserMeeting(host,meeting, MeetingRole.HOST));

        int totalRequestCount = 100;
        List<AuthUser> authUsers = new ArrayList<>();
        for (int i = 0; i < totalRequestCount; i++) {
            User user = userRepository.saveAndFlush(new User(
                    "test" + i + "@example.com",
                    "pass!",
                    "테스터" + i, LocalDate.of(1995, 1, 1),
                    false,
                    address,
                    UserRole.USER
            ));
            authUsers.add(new AuthUser(user.getId(), user.getEmail(), user.getNickname(), user.getRole()));
        }

        // 4. 동시 요청 실행
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(totalRequestCount);

        for (AuthUser authUser : authUsers) {
            executorService.submit(() -> {
                try {
                    attendanceFacade.registerAttendance(authUser, meetingId);
                } catch (Exception e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long finalAttendeeCount = userMeetingRepository.countByMeetingId(meetingId);
        Meeting updatedMeeting = meetingRepository.findById(meetingId).orElseThrow();

        System.out.println("최종 참여 인원(방장 포함): " + finalAttendeeCount);
        System.out.println("모임 최종 상태: " + updatedMeeting.getStatus());

        // 방장이 1명 미리 있으므로, 일반 유저 9명이 더 들어와서 총 10명이 되어야 함
        assertThat(finalAttendeeCount).isEqualTo(10);
        assertThat(updatedMeeting.getStatus()).isEqualTo(MeetingStatus.CLOSED);
    }
}