package com.example.burnchuck;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.enums.UserRole;
import com.example.burnchuck.domain.meeting.repository.UserMeetingRepository;
import com.example.burnchuck.domain.meeting.service.AttendanceFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AttendanceConcurrencyTest {

    @Autowired
    private AttendanceFacade attendanceFacade;

    @Autowired
    private UserMeetingRepository userMeetingRepository;

    @Test
    @DisplayName("분산 락을 활용하여 수많은 동시 요청에도 정원 초과 없이 정확한 인원만 참여되어야 한다")
    void concurrencyTest() throws InterruptedException {

        int totalRequestCount = 39;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(totalRequestCount);

        long targetMeetingId = 1L;

        for (int i = 0; i < totalRequestCount; i++) {
            long userId = (long) (i + 10);

            executorService.submit(() -> {
                try {
                    AuthUser testUser = new AuthUser(userId, "test" + userId + "@example.com", "테스터" + userId, UserRole.USER);
                    attendanceFacade.registerAttendance(testUser, targetMeetingId);
                } catch (Exception e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long finalAttendeeCount = userMeetingRepository.countByMeetingId(targetMeetingId);
        System.out.println("최종 DB 기록 인원: " + finalAttendeeCount);

        assertThat(finalAttendeeCount).isEqualTo(10);
    }
}