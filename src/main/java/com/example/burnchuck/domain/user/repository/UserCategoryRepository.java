package com.example.burnchuck.domain.user.repository;

import com.example.burnchuck.common.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
}
