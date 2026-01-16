package com.example.burnchuck.domain.follow.model.dto;

import com.example.burnchuck.common.entity.Follow;
import com.example.burnchuck.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowDto {

    private Long id;
    private User follower;
    private User followee;

    public static FollowDto from(Follow follow) {
        return new FollowDto(
                follow.getId(),
                follow.getFollower(),
                follow.getFollowee()
        );
    }
}
