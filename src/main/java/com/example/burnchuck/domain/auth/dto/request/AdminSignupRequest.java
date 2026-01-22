package com.example.burnchuck.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminSignupRequest extends AuthSignupRequest {

    private String adminKey;
}
