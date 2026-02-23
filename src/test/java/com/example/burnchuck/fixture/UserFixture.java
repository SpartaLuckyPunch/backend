package com.example.burnchuck.fixture;

import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.Provider;
import com.example.burnchuck.common.enums.UserRole;
import java.util.UUID;

public class UserFixture {

    public static final Address address = new Address("서울특별시", "강동구", "천호동", 37.5450159, 127.1368066);

    public static User testUser() {
        return new User(
            "test" + UUID.randomUUID() + "@test.com",
            "1234",
            "testUser",
            null,
            null,
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
    }

    public static User testUser2() {
        return new User(
            "test" + UUID.randomUUID() + "@test.com",
            "1234",
            "testUser2",
            null,
            null,
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
    }
}
