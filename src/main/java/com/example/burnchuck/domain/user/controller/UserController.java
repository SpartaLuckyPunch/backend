package com.example.burnchuck.domain.user.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.user.model.request.*;
import com.example.burnchuck.domain.user.model.response.UserUpdateProfileResponse;
import com.example.burnchuck.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 수정(닉네임, 주소)
     *
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
    @PutMapping
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
}
