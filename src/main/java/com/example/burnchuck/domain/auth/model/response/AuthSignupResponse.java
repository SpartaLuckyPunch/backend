package com.example.burnchuck.domain.auth.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthSignupResponse {

    private final String token;
}
