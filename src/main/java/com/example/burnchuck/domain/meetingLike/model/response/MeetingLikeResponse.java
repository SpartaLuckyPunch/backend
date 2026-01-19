package com.example.burnchuck.domain.meetingLike.model.response;

import com.example.burnchuck.common.entity.MeetingLike;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingLikeResponse {

    private final Long meetingId;

    public static MeetingLikeResponse from(MeetingLike meetingLike) {
        return new MeetingLikeResponse(
                meetingLike.getId()
        );
    }
}
