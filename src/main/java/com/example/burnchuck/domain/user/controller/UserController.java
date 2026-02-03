package com.example.burnchuck.domain.user.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.USER_DELETE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.USER_GET_ADDRESS_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.USER_GET_PROFILE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.USER_UPDATE_PASSWORD_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.USER_UPDATE_PROFILE_SUCCESS;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.user.dto.request.UserUpdatePasswordRequest;
import com.example.burnchuck.domain.user.dto.request.UserUpdateProfileRequest;
import com.example.burnchuck.domain.user.dto.response.UserGetAddressResponse;
import com.example.burnchuck.domain.user.dto.response.UserGetProfileReponse;
import com.example.burnchuck.domain.user.dto.response.UserUpdateProfileResponse;
import com.example.burnchuck.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 수정(닉네임, 주소)
     * 고도화 작업 시, 프로필 이미지 수정 항목 추가 예정
     */
    @Operation(
            summary = "내 정보 수정",
            description = """
                    나의 닉네임과 주소를 수정할 수 있습니다.
                    """
    )
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
    @Operation(
            summary = "비밀번호 변경",
            description = """
                    이전 비밀번호를 통해 새 비밀번호로 변경할 수 있습니다.
                    """
    )
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
    @Operation(
            summary = "회원 탈퇴",
            description = """
                    해당 계정을 탈퇴합니다.
                    탈퇴한 계정은 논리적으로 삭제됩니다.
                    """
    )
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
    @Operation(
            summary = "프로필 조회",
            description = """
                    특정 사용자의 프로필을 조회할 수 있습니다.
                    """
    )
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserGetProfileReponse>> getProfile(
        @PathVariable Long userId
    ) {
        UserGetProfileReponse response = userService.getProfile(userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(USER_GET_PROFILE_SUCCESS, response));
    }

    /**
     * 주소 조회
     */
    @Operation(
        summary = "주소 조회",
        description = """
                    로그인한 사용자의 주소을 조회할 수 있습니다.
                    """
    )
    @GetMapping("/address")
    public ResponseEntity<CommonResponse<UserGetAddressResponse>> getAddress(
        @AuthenticationPrincipal AuthUser authUser
    ) {
        UserGetAddressResponse response = userService.getAddress(authUser);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(USER_GET_ADDRESS_SUCCESS, response));
    }
}
