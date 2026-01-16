package com.example.burnchuck.domain.follow.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.follow.model.response.FollowResponse;
import com.example.burnchuck.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.FOLLOW_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.UNFOLLOW_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FollowController {

    private final FollowService followService;

    /**
     * 팔로우
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<CommonResponse<FollowResponse>> follow(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long userId
    ) {
        FollowResponse response = followService.follow(user, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(FOLLOW_SUCCESS, response));
    }

    /**
     * 언팔로우
     */
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<CommonResponse<Void>> unfollow(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long userId
    ) {
        followService.unfollow(user, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(UNFOLLOW_SUCCESS));
    }
}
