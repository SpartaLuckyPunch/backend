package com.example.burnchuck.domain.follow.model.response;

import com.example.burnchuck.domain.follow.model.dto.FollowDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponse {

    private final Long followerId;
    private final Long followeeId;

    public static FollowResponse from(FollowDto dto) {
        return new FollowResponse(
                dto.getFollower().getId(),
                dto.getFollowee().getId()
        );
    }
}
