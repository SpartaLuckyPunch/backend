package com.example.burnchuck.domain.meetingLike.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.domain.meetingLike.dto.response.MeetingLikeCountResponse;
import com.example.burnchuck.domain.meetingLike.dto.response.MeetingLikeResponse;
import com.example.burnchuck.domain.meetingLike.service.MeetingLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetings")
@Tag(name = "Like(Meeting)")
public class MeetingLikeController {

    private final MeetingLikeService meetingLikeService;

    /**
     *  좋아요 생성
     */
    @Operation(
            summary = "좋아요 생성",
            description = """
                    모임 게시글에 좋아요를 생성합니다.
                    """
    )
    @PostMapping("/{meetingId}/likes")
    public ResponseEntity<CommonResponse<MeetingLikeResponse>> createLike(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long meetingId
    ) {
        MeetingLikeResponse response = meetingLikeService.createLike(user, meetingId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(LIKE_CREATE_SUCCESS, response));
    }

    /**
     *  좋아요 취소
     */
    @Operation(
            summary = "좋아요 취소",
            description = """
                    모임 게시글에 좋아요를 취소합니다.
                    """
    )
    @DeleteMapping("/{meetingId}/likes")
    public ResponseEntity<CommonResponse<Void>> deleteLike(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long meetingId
    ) {
        meetingLikeService.deleteLike(user, meetingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(LIKE_CANCEL_SUCCESS));
    }

    /**
     *  모임 별 좋아요 개수 조회
     */
    @Operation(
            summary = "모임 별 좋아요 개수 조회",
            description = """
                    특정 모임의 좋아요 개수를 조회합니다.
                    """
    )
    @GetMapping("/{meetingId}/likes")
    public ResponseEntity<CommonResponse<MeetingLikeCountResponse>> countLikes(
            @PathVariable Long meetingId
    ) {
        MeetingLikeCountResponse response = meetingLikeService.countLikes(meetingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(LIKE_COUNT_SUCCESS, response));
    }
}
