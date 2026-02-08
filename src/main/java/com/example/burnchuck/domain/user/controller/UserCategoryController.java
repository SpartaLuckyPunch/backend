package com.example.burnchuck.domain.user.controller;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.user.dto.request.UserCategoryCreateRequest;
import com.example.burnchuck.domain.user.dto.response.UserCategoryCreateResponse;
import com.example.burnchuck.domain.user.service.UserCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.burnchuck.common.enums.SuccessMessage.USER_UPDATE_PROFILE_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/categories")
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    @PostMapping
    public ResponseEntity<CommonResponse<UserCategoryCreateResponse>> createUserFavoriteCategory(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody UserCategoryCreateRequest request
    ) {
        UserCategoryCreateResponse response = userCategoryService.createUserFavoriteCategory(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(USER_UPDATE_PROFILE_SUCCESS, response));
    }
}
