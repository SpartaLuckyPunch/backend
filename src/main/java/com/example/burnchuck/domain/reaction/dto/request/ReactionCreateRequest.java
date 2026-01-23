package com.example.burnchuck.domain.reaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionCreateRequest {

    @NotBlank(message = "리액션 종류는 필수입니다.")
    private String reaction;
}
