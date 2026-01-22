package com.example.burnchuck.domain.auth.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.AUTH_LOGIN_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.AUTH_SIGNUP_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.model.request.*;
import com.example.burnchuck.domain.auth.model.response.*;
import com.example.burnchuck.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<AuthSignupResponse>> signup(
        @Valid @RequestBody AuthSignupRequest request
    ) {
        AuthSignupResponse response = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CommonResponse.success(AUTH_SIGNUP_SUCCESS, response));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> login(
        @Valid @RequestBody AuthLoginRequest request
    ) {
        AuthLoginResponse response = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(AUTH_LOGIN_SUCCESS, response));
    }
}
