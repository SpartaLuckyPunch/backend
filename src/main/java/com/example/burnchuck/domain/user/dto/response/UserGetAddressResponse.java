package com.example.burnchuck.domain.user.dto.response;

import com.example.burnchuck.common.entity.Address;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserGetAddressResponse {

    private final String province;
    private final String city;
    private final String district;

    public static UserGetAddressResponse from(Address address) {
        return new UserGetAddressResponse(
            address.getCity(),
            address.getCity(),
            address.getDistrict()
        );
    }
}
