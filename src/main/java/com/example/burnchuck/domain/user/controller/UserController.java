package com.example.burnchuck.domain.user.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.domain.user.dto.request.*;
import com.example.burnchuck.domain.user.dto.response.UserGetProfileReponse;
import com.example.burnchuck.domain.user.dto.response.UserUpdateProfileResponse;
import com.example.burnchuck.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 수정(닉네임, 주소)
     * <p>
     * 고도화 작업 시, 프로필 이미지 수정 항목 추가 예정
     */
    @PatchMapping
    public ResponseEntity<CommonResponse<UserUpdateProfileResponse>> updateProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserUpdateProfileRequest request
    ) {
        UserUpdateProfileResponse response = userService.updateProfile(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(USER_UPDATE_PROFILE_SUCCESS, response));
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/password")
    public ResponseEntity<CommonResponse<Void>> updatePassword(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserUpdatePasswordRequest request
    ) {
        userService.updatePassword(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(USER_UPDATE_PASSWORD_SUCCESS));
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> deleteUser(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        userService.deleteUser(authUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(USER_DELETE_SUCCESS));
    }

    /**
     * 프로필 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserGetProfileReponse>> getProfile(
            @PathVariable Long userId
    ) {
        UserGetProfileReponse response = userService.getProfile(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(USER_GET_PROFILE_SUCCESS, response));
    }

}