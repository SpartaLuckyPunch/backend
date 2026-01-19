package com.example.burnchuck.domain.reaction.repository;

import com.example.burnchuck.common.entity.Reaction;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    default Reaction findReactionById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.REACTION_NOT_FOUND));
    }
}
