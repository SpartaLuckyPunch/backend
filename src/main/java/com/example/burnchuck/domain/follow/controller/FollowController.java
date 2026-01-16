package com.example.burnchuck.domain.follow.controller;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.domain.follow.model.response.FollowResponse;
import com.example.burnchuck.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.burnchuck.common.enums.SuccessMessage.FOLLOW_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<CommonResponse<FollowResponse>> follow(
            @PathVariable Long userId
    ) {
        FollowResponse response = followService.follow(userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(FOLLOW_SUCCESS, response));
    }
}
