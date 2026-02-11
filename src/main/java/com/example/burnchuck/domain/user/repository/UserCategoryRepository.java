package com.example.burnchuck.domain.user.repository;

import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    List<UserCategory> findByUser(User user);
}
