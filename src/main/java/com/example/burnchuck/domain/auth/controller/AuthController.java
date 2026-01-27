package com.example.burnchuck.domain.auth.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.AUTH_LOGIN_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.AUTH_SIGNUP_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.dto.request.*;
import com.example.burnchuck.domain.auth.dto.response.*;
import com.example.burnchuck.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @Operation(
            summary = "회원가입",
            description = """
                    필요한 정보들을 입력하여 새로운 사용자를 생성합니다.
                    """
    )
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
    @Operation(
            summary = "로그인",
            description = """
                    등록된 이메일과 비밀번호를 입력하여 토큰을 발급받습니다.
                    
                     - 토큰 만료 : 1시간
                    """
    )
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> login(
        @Valid @RequestBody AuthLoginRequest request
    ) {
        AuthLoginResponse response = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(AUTH_LOGIN_SUCCESS, response));
    }
}
