package com.example.burnchuck.domain.user.controller;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.user.dto.request.UserCategoryCreateRequest;
import com.example.burnchuck.domain.user.dto.response.UserCategoryGetResponse;
import com.example.burnchuck.domain.user.service.UserCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    /**
     * 관심 카테고리 등록
     */
    @PostMapping("/categories")
    public ResponseEntity<CommonResponse<Void>> createUserFavoriteCategory(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody UserCategoryCreateRequest request
    ) {
        userCategoryService.createUserFavoriteCategory(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(USER_POST_FAVORITE_CATEGORY_SUCCESS));
    }

    /**
     * 관심 카테고리 조회
     */
    @GetMapping("/{userId}/categories")
    public ResponseEntity<CommonResponse<UserCategoryGetResponse>> getUserFavoriteCategory(
            @PathVariable Long userId
    ) {
        UserCategoryGetResponse response = userCategoryService.getUserFavoriteCategory(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(USER_GET_FAVORITE_CATEGORY_SUCCESS, response));
    }
}
