package com.example.burnchuck.domain.review.repository;

import com.example.burnchuck.common.entity.ReviewReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {}