package com.example.burnchuck.domain.review.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionCount {

    private final String reaction;
    private final Long count;

    public static ReactionCount of(String reaction, Long count) {
        return new ReactionCount(reaction, count);
    }
}
