package com.example.burnchuck.domain.review.repository;

import com.example.burnchuck.common.entity.Review;
import com.example.burnchuck.common.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByReviewee(User user);
}
