package com.example.burnchuck.domain.review.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionResponse {

    private final Long reactionId;
    private final String reaction;
}
