package com.example.burnchuck.domain.meetingLike.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meetingLike.model.response.MeetingLikeCountResponse;
import com.example.burnchuck.domain.meetingLike.model.response.MeetingLikeResponse;
import com.example.burnchuck.domain.meetingLike.service.MeetingLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meetings")
public class MeetingLikeController {

    private final MeetingLikeService meetingLikeService;

    @PostMapping("/{meetingId}/likes")
    public ResponseEntity<CommonResponse<MeetingLikeResponse>> createLike(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long meetingId
    ) {
        MeetingLikeResponse response = meetingLikeService.createLike(user, meetingId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(LIKE_SUCCESS, response));
    }

    @DeleteMapping("/{meetingId}/likes")
    public ResponseEntity<CommonResponse<Void>> deleteLike(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long meetingId
    ) {
        meetingLikeService.deleteLike(user, meetingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(LIKE_CANCEL_SUCCESS));
    }

    @GetMapping("/{meetingId}/likes")
    public ResponseEntity<CommonResponse<MeetingLikeCountResponse>> countLikes(
            @PathVariable Long meetingId
    ) {
        MeetingLikeCountResponse response = meetingLikeService.countLikes(meetingId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(LIKE_COUNT_SUCCESS, response));
    }
}
