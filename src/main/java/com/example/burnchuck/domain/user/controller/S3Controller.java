package com.example.burnchuck.domain.user.controller;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.GetS3Url;
import com.example.burnchuck.domain.user.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class S3Controller {

    private final S3Service s3Service;


    /**
     * 프로필 이미지 등록 (Post)
     */
    @GetMapping("/profile/img/post")
    public ResponseEntity<CommonResponse<GetS3Url>> getPostS3Url(
            @AuthenticationPrincipal AuthUser authUser,
            String filename
    ) {
        GetS3Url response = s3Service.getPostS3Url(authUser, filename);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(USER_PROFILE_IMG_PUT_LINK_SUCCESS, response));
    }

    /**
     * 프로필 이미지 등록 (Get)
     */
    @GetMapping("/profile/img/get")
    public ResponseEntity<CommonResponse<GetS3Url>> getGetS3Url(
            @RequestParam String key
    ) {
        GetS3Url response = s3Service.getGetS3Url(key);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(USER_PROFILE_IMG_GET_LINK_SUCCESS, response));
    }
}