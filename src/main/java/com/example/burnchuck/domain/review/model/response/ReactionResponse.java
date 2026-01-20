package com.example.burnchuck.domain.review.model.response;

import com.example.burnchuck.common.entity.ReviewReaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionResponse {

    private final Long reactionId;
    private final String reaction;

    public static ReactionResponse from(ReviewReaction reviewReaction) {
        return new ReactionResponse(
                reviewReaction.getReaction().getId(),
                reviewReaction.getReaction().getReaction()
        );
    }
}
