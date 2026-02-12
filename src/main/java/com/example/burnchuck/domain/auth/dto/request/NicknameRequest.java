package com.example.burnchuck.domain.auth.dto.request;

import com.example.burnchuck.common.enums.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameRequest {

    @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
    @Size(max = 50, message = ValidationMessage.USERNAME_SIZE)
    private String nickname;
}
