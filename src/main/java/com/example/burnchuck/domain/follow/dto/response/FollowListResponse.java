package com.example.burnchuck.domain.follow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FollowListResponse {

    private List<FollowUserDto> followUserList;

    @Getter
    @AllArgsConstructor
    public static class FollowUserDto {
        private Long userId;
        private String nickname;
    }
}
