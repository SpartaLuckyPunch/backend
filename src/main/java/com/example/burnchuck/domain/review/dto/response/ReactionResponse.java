package com.example.burnchuck.domain.review.dto.response;

import com.example.burnchuck.common.entity.Reaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionResponse {

    private final Long reactionId;
    private final String reaction;

    public static ReactionResponse from(Reaction reaction) {
        return new ReactionResponse(
                reaction.getId(),
                reaction.getReaction()
        );
    }
}
