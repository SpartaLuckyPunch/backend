package com.example.burnchuck.domain.reaction.dto.response;

import com.example.burnchuck.common.entity.Reaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionCreateResponse {

    private Long id;
    private String reaction;

    public static ReactionCreateResponse from(Reaction reaction) {
        return new ReactionCreateResponse(
                reaction.getId(),
                reaction.getReaction()
        );
    }
}
