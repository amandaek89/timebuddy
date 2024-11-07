package com.timebuddy.services;

import com.timebuddy.dtos.UserRequestDto;
import com.timebuddy.dtos.UserResponseDto;
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
     * Retrieves a user by their ID and returns a UserResponseDto.
     *
     * @param id The ID of the user to be retrieved.
     * @return An Optional containing the UserResponseDto if found, otherwise an empty Optional.
     */
    public Optional<UserResponseDto> findUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponseDto);
    }

    /**
     * Retrieves a user by their username and returns a UserResponseDto.
     *
     * @param username The username of the user to be retrieved.
     * @return An Optional containing the UserResponseDto if found, otherwise an empty Optional.
     */
    public Optional<UserResponseDto> loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToResponseDto);
    }

    /**
     * Updates the user's information.
     *
     * @param id            The ID of the user to be updated.
     * @param updatedUser   The updated UserRequestDto containing the new values.
     * @return The updated UserResponseDto, or an empty Optional if no user is found with the given ID.
     */
    public Optional<UserResponseDto> updateUser(Long id, UserRequestDto updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    // Update user fields
                    user.setUsername(updatedUser.getUsername());
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPassword(updatedUser.getPassword()); // Only update password if provided
                    }
                    user.setUpdatedAt(new java.util.Date()); // Set the updated timestamp
                    User updatedUserEntity = userRepository.save(user);
                    return convertToResponseDto(updatedUserEntity);
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

    /**
     * Converts a User entity to a UserResponseDto.
     *
     * @param user The User entity to convert.
     * @return The corresponding UserResponseDto.
     */
    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
        return userResponseDto;
    }
}



