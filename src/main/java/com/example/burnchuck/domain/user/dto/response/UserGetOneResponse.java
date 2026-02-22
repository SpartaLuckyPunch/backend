package com.example.burnchuck.domain.user.dto.response;

import com.example.burnchuck.common.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserGetOneResponse {

    public final Long userId;
    public final String email;
    public final String nickname;
    public final UserRole userRole;

}
