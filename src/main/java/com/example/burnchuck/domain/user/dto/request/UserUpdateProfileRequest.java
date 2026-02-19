package com.example.burnchuck.domain.user.dto.request;

import com.example.burnchuck.common.constants.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateProfileRequest {

    @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
    @Size(max = 50, message = ValidationMessage.USERNAME_SIZE)
    private String nickname;

    @NotBlank(message = ValidationMessage.PROVINCE_NOT_BLANK)
    private String province;

    @NotBlank(message = ValidationMessage.CITY_NOT_BLANK)
    private String city;

    private String district;

    @Pattern(
        regexp = "^(http|https)://.*$",
        message = ValidationMessage.IMG_URL_FORMAT
    )
    private String profileImgUrl;
}
