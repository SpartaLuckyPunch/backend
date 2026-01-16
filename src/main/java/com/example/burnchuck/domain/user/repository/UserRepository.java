package com.example.burnchuck.domain.user.repository;

import com.example.burnchuck.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
