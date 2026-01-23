package com.example.burnchuck.domain.reaction.dto.response;

import com.example.burnchuck.common.entity.Reaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminReactionCreateResponse {

    private Long id;
    private String reaction;

    public static AdminReactionCreateResponse from(Reaction reaction) {
        return new AdminReactionCreateResponse(
                reaction.getId(),
                reaction.getReaction()
        );
    }
}
