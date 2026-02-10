package com.example.burnchuck.domain.user.dto.response;

import com.example.burnchuck.common.enums.UserRole;
import lombok.Getter;

@Getter
public class UserGetOneResponse {

    public final Long userId;
    public final String email;
    public final String nickname;
    public final UserRole userRole;

    public UserGetOneResponse(Long userId, String email, String nickname, UserRole userRole) {

        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.userRole = userRole;
    }
}
