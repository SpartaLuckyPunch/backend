package com.example.burnchuck.domain.auth.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.dto.request.EmailConfirmRequest;
import com.example.burnchuck.domain.auth.dto.request.EmailRequest;
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
    public ResponseEntity<CommonResponse<Boolean>> sendVerificationEmail(
            @Valid @RequestBody EmailRequest request
    ) {
        boolean result = emailService.sendVerificationEmail(request.getEmail());

        return ResponseEntity.ok(CommonResponse.success(AUTH_EMAIL_SEND_SUCCESS, result));
    }

    /**
     * 이메일 인증 번호 확인
     */
    @PostMapping("/email-verifications/confirm")
    public ResponseEntity<CommonResponse<Boolean>> confirmCode(
            @Valid @RequestBody EmailConfirmRequest request
    ) {
        boolean isVerified = emailService.verifyCode(request.getEmail(), request.getVerificationCode());

        return ResponseEntity.ok(CommonResponse.success(AUTH_EMAIL_VERIFY_SUCCESS, isVerified));
    }
}
