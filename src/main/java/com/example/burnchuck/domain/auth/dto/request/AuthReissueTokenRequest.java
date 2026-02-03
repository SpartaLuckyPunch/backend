package com.example.burnchuck.domain.auth.dto.request;

import com.example.burnchuck.common.enums.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthReissueTokenRequest {

    @NotBlank(message = ValidationMessage.REFRESH_TOKEN_NOT_BLANK)
    private String refreshToken;
}
