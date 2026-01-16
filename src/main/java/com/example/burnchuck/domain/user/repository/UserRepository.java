package com.example.burnchuck.domain.user.repository;

import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    default User findUserByEmail(String email) {
        return findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    default User findUserById(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    default User findActivateUserById(Long id, ErrorCode errorCode) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(errorCode));
    }
}
