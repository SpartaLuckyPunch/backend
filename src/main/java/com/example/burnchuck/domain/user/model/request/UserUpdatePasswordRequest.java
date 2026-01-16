package com.example.burnchuck.domain.user.model.request;

import com.example.burnchuck.common.enums.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdatePasswordRequest {

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    private String oldPassword;

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    private String newPassword;
}
