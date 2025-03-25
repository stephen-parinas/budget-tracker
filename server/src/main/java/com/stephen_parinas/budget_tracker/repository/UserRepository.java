package com.stephen_parinas.budget_tracker.repository;

import com.stephen_parinas.budget_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository interface for managing {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Find a user by their email address.
     *
     * @param email The email address.
     * @return The user entity.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by their verification code.
     *
     * @param verificationCode The verification code entered by the user.
     * @return The user entity.
     */
    Optional<User> findByVerificationCode(String verificationCode);
}
