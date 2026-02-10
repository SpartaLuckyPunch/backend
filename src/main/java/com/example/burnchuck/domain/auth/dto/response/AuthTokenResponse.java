package com.example.burnchuck.domain.auth.dto.response;

import com.example.burnchuck.common.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthTokenResponse {

    private final String token;
    private final String refreshToken;

    private final Long userId;
    private final String email;
    private final String nickname;
    private final UserRole userRole;
}
