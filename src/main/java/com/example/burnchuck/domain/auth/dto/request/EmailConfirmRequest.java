package com.example.burnchuck.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailConfirmRequest {

    private String email;
    private String verificationCode;
}
