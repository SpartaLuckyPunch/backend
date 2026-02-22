package com.example.burnchuck.domain.follow.dto.response;

import com.example.burnchuck.common.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponse {

    private final Long followerId;
    private final Long followeeId;

    public static FollowResponse from(Follow follow) {
        return new FollowResponse(
                follow.getFollower().getId(),
                follow.getFollowee().getId()
        );
    }
}
