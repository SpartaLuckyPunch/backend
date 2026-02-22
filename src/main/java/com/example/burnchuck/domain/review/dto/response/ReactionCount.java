package com.example.burnchuck.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionCount {

    private final String reaction;
    private final Long count;
}
