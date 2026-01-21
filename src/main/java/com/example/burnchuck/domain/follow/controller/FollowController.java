package com.example.burnchuck.domain.follow.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.follow.model.response.FollowCountResponse;
import com.example.burnchuck.domain.follow.model.response.FollowListResponse;
import com.example.burnchuck.domain.follow.model.response.FollowResponse;
import com.example.burnchuck.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

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

    /**
     * 팔로잉 / 팔로워 수 조회
     */
    @GetMapping("/{userId}/follow-count")
    public ResponseEntity<CommonResponse<FollowCountResponse>> followCount(
            @PathVariable Long userId
    ) {
        FollowCountResponse response = followService.followCount(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(FOLLOW_GET_SUCCESS, response));
    }

    /**
     * 팔로잉 목록 조회
     */
    @GetMapping("/{userId}/following-list")
    public ResponseEntity<CommonResponse<FollowListResponse>> getFollowingList(
            @PathVariable Long userId
    ) {
        FollowListResponse response = followService.followingList(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(FOLLOW_GET_FOLLOWING_SUCCESS, response));
    }

    /**
     * 팔로워 목록 조회
     */
    @GetMapping("/{userId}/follower-list")
    public ResponseEntity<CommonResponse<FollowListResponse>> getFollowerList(
            @PathVariable Long userId
    ) {
        FollowListResponse response = followService.followerList(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(FOLLOW_GET_FOLLOWER_SUCCESS, response));
    }
}
