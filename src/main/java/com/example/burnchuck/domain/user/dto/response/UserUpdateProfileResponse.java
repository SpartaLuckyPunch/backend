package com.example.burnchuck.domain.user.dto.response;

import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserUpdateProfileResponse {

    private final String nickname;
    private final String province;
    private final String city;
    private final String district;

    public static UserUpdateProfileResponse from(User user, Address address) {
        return new UserUpdateProfileResponse(
            user.getNickname(),
            address.getProvince(),
            address.getCity(),
            address.getDistrict()
        );
    }
}
