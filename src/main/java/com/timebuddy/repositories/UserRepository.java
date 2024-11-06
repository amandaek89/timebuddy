package com.timebuddy.repositories;

import com.timebuddy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for interacting with the User database table.
 * Provides methods to find users by username, and save or retrieve users by ID.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * This method returns an Optional<User> in case the user is not found, avoiding null results.
     *
     * @param username The username of the user to find.
     * @return An Optional containing the User if found, or an empty Optional if not found.
     */
    Optional<User> findByUsername(String username);
}
