package com.example.burnchuck.domain.follow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowCountResponse {

    private final long following;
    private final long followers;

    public static FollowCountResponse of(long followings, long followers) {
        return new FollowCountResponse(
                followings,
                followers
        );
    }

}
