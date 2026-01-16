package com.example.burnchuck.domain.review.repository;

import com.example.burnchuck.common.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
