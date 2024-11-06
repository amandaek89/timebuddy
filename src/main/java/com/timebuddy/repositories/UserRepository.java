package com.timebuddy.repositories;

import com.timebuddy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the User entity.
 * Provides CRUD operations for User objects and custom query methods.
 * Extends JpaRepository to benefit from built-in functionality for database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their username.
     * This method will automatically generate a query based on the method name.
     *
     * @param username The username of the User to search for.
     * @return The User object that matches the provided username, or null if no match is found.
     */
    User findByUsername(String username);
}
