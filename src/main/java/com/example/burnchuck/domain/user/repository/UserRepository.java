package com.example.burnchuck.domain.user.repository;

import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.Provider;
import com.example.burnchuck.common.exception.CustomException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    @Query("""
        SELECT u
        FROM User u JOIN FETCH u.address
        WHERE u.id = :id AND u.isDeleted = false
        """)
    Optional<User> findActiveUserByIdWithAddress(@Param("id") Long id);

    default User findActivateUserByEmail(String email) {
        return findByEmailAndIsDeletedFalse(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    default User findActivateUserById(Long id) {
        return findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // NOT_FOUND_USER 외 다른 예외 사용 시, 해당 메서드 사용
    default User findActivateUserById(Long id, ErrorCode errorCode) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(errorCode));
    }

    default User findActivateUserWithAddress(Long id) {
        return findActiveUserByIdWithAddress(id)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
