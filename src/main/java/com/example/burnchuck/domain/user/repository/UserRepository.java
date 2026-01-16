package com.example.burnchuck.domain.user.repository;

import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    default User findActivateUserById(Long id, ErrorCode errorCode) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(errorCode));
    }
}
