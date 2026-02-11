package com.example.burnchuck.domain.auth.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.dto.request.EmailConfirmRequest;
import com.example.burnchuck.domain.auth.dto.request.EmailRequest;
import com.example.burnchuck.domain.auth.dto.request.NicknameRequest;
import com.example.burnchuck.domain.auth.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    /**
     * 이메일 인증 번호 발송
     */
    @PostMapping("/email-verifications")
    public ResponseEntity<CommonResponse<Void>> sendVerificationEmail(
            @Valid @RequestBody EmailRequest request
    ) {
        emailService.sendVerificationEmail(request.getEmail());

        return ResponseEntity.ok(CommonResponse.successNodata(AUTH_EMAIL_SEND_SUCCESS));
    }

    /**
     * 이메일 인증 번호 확인
     */
    @PostMapping("/email-verifications/confirm")
    public ResponseEntity<CommonResponse<Void>> confirmCode(
            @Valid @RequestBody EmailConfirmRequest request
    ) {
        emailService.verifyCode(request.getEmail(), request.getVerificationCode());

        return ResponseEntity.ok(CommonResponse.successNodata(AUTH_EMAIL_VERIFY_SUCCESS));
    }

    /**
     * 닉네임 중복 확인
     */
    @PostMapping("/nickname-availability")
    public ResponseEntity<CommonResponse<Void>> checkNickname(
            @Valid @RequestBody NicknameRequest request
    ) {
        emailService.checkNicknameAvailable(request.getNickname());

        return ResponseEntity.ok(CommonResponse.successNodata(AUTH_NICKNAME_AVAILABLE));
    }
}
