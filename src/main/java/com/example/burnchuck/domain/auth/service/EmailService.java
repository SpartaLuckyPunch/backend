package com.example.burnchuck.domain.auth.service;

import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    /**
     * 이메일 인증 번호 발송
     */
    @Async
    public void sendVerificationEmail(String email) {
        try {
            String verificationCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
            redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[번쩍] 이메일 인증 번호입니다.");
            helper.setText("인증 번호는 <b>" + verificationCode + "</b> 입니다.<br>5분 이내에 입력해주세요.", true);

            mailSender.send(message);
        } catch (MessagingException e) {

            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 이메일 인증 번호 확인
     */
    public void verifyCode(String email, String code) {
        String savedCode = redisTemplate.opsForValue().get(email);

        if (savedCode == null || !savedCode.equals(code)) {

            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisTemplate.delete(email);
    }

    /**
     * 닉네임 중복 확인
     */
    public void checkNicknameAvailable(String nickname) {
        // 이미 존재하면 예외 발생
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_EXIST);
        }
    }
}