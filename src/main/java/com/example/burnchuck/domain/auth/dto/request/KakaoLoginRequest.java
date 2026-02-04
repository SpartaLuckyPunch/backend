package com.example.burnchuck.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest {

    @Schema(description = "카카오 액세스 토큰", example = "카카오에서 받은 access_token")
    @NotBlank(message = "액세스 토큰은 필수입니다.")
    private String accessToken;
}
