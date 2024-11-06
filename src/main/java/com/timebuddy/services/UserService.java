package com.timebuddy.services;

import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
/**
 * Service class for managing users.
 * Provides methods to register users, find users by ID, and load users by their username for authentication.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructor for UserService.
     *
     * @param userRepository The repository used to interact with the User database table.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user by saving the user details to the database.
     *
     * @param user The user to be registered.
     * @return The saved user with its generated ID.
     */
    public User registerUser(User user) {
        // Set creation and update timestamps
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());

        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to be retrieved.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to be retrieved.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Updates the user's information.
     *
     * @param id The ID of the user to be updated.
     * @param updatedUser The updated User object containing the new values.
     * @return The updated User object, or an empty Optional if no user is found with the given ID.
     */
    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setPassword(updatedUser.getPassword());
                    user.setUpdatedAt(new java.util.Date()); // Set the updated timestamp
                    return userRepository.save(user);
                });
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to be deleted.
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

