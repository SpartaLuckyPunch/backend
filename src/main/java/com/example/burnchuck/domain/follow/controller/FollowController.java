package com.example.burnchuck.domain.follow.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.FOLLOW_CREATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.FOLLOW_DELETE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.FOLLOW_GET_EXISTENCE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.FOLLOW_GET_FOLLOWER_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.FOLLOW_GET_FOLLOWING_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.FOLLOW_GET_SUCCESS;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.domain.follow.dto.response.FollowCountResponse;
import com.example.burnchuck.domain.follow.dto.response.FollowListResponse;
import com.example.burnchuck.domain.follow.dto.response.FollowResponse;
import com.example.burnchuck.domain.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Follow")
public class FollowController {

    private final FollowService followService;

    /**
     * 팔로우
     */
    @Operation(
            summary = "팔로우",
            description = """
                    로그인한 유저를 기준으로 특정 사용자를 팔로우하는 객체를 생성합니다..
                    """
    )
    @PostMapping("/{userId}/follow")
    public ResponseEntity<CommonResponse<FollowResponse>> follow(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long userId
    ) {
        FollowResponse response = followService.follow(user, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(FOLLOW_CREATE_SUCCESS, response));
    }

    /**
     * 언팔로우
     */
    @Operation(
            summary = "언팔로우",
            description = """
                    로그인한 유저를 기준으로 특정 사용자를 팔로우하는 객체를 삭제합니다.
                    """
    )
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<CommonResponse<Void>> unfollow(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long userId
    ) {
        followService.unfollow(user, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(FOLLOW_DELETE_SUCCESS));
    }

    /**
     * 팔로잉 / 팔로워 수 조회
     */
    @Operation(
            summary = "팔로잉 / 팔로워 수 조회",
            description = """
                    특정 사용자에 대한 팔로잉 / 팔로워 수를 조회합니다.
                    """
    )
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
    @Operation(
            summary = "팔로잉 목록 조회",
            description = """
                    특정 사용자를 팔로잉하는 목록을 조회합니다.
                    """
    )
    @GetMapping("/{userId}/followings")
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
    @Operation(
            summary = "팔로워 목록 조회",
            description = """
                    특정 사용자가 팔로우하는 목록을 조회합니다.
                    """
    )
    @GetMapping("/{userId}/followers")
    public ResponseEntity<CommonResponse<FollowListResponse>> getFollowerList(
            @PathVariable Long userId
    ) {
        FollowListResponse response = followService.followerList(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(FOLLOW_GET_FOLLOWER_SUCCESS, response));
    }

    /**
     * 팔로우 여부 확인
     */
    @Operation(
        summary = "팔로우 여부 확인",
        description = """
                    특정 사용자를 팔로우하고 있는지 조회합니다.
                    """
    )
    @GetMapping("/{userId}/follow-existence")
    public ResponseEntity<CommonResponse<Boolean>> checkFollowExistence(
        @PathVariable Long userId,
        @AuthenticationPrincipal AuthUser authUser
    ) {
        boolean response = followService.checkFollowExistence(userId, authUser);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(FOLLOW_GET_EXISTENCE_SUCCESS, response));
    }
}
