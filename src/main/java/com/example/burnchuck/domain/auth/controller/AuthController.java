package com.example.burnchuck.domain.auth.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.AUTH_LOGIN_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.AUTH_REISSUE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.AUTH_SIGNUP_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.enums.Provider;
import com.example.burnchuck.domain.auth.dto.request.AuthLoginRequest;
import com.example.burnchuck.domain.auth.dto.request.AuthReissueTokenRequest;
import com.example.burnchuck.domain.auth.dto.request.AuthSignupRequest;
import com.example.burnchuck.domain.auth.dto.request.KakaoLoginRequest;
import com.example.burnchuck.domain.auth.dto.response.AuthTokenResponse;
import com.example.burnchuck.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CommonResponse<AuthTokenResponse>> signup(
        @Valid @RequestBody AuthSignupRequest request
    ) {
        AuthTokenResponse response = authService.signup(request);

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
    public ResponseEntity<CommonResponse<AuthTokenResponse>> login(
        @Valid @RequestBody AuthLoginRequest request
    ) {
        AuthTokenResponse response = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(AUTH_LOGIN_SUCCESS, response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<AuthTokenResponse>> reissueToken(
        @Valid @RequestBody AuthReissueTokenRequest request
    ) {
        AuthTokenResponse response = authService.reissueToken(request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(AUTH_REISSUE_SUCCESS, response));
    }

    /**
     * 카카오 소셜 로그인
     */
    @Operation(
            summary = "카카오 로그인",
            description = """
                    프론트엔드에서 전달받은 인가 코드(code)를 이용하여 로그인을 진행합니다. 
                    백엔드에서 직접 카카오 토큰을 교환하여 보안을 강화했습니다.
                    """
    )
    @PostMapping("/kakao")
    public ResponseEntity<CommonResponse<AuthTokenResponse>> kakaoLogin(
            @Valid @RequestBody KakaoLoginRequest request
    ) {
        AuthTokenResponse response = authService.socialLogin(request.getCode(), Provider.KAKAO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(AUTH_LOGIN_SUCCESS, response));
    }

}

