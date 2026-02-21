package com.example.burnchuck.domain.reaction.repository;

import com.example.burnchuck.common.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
}
