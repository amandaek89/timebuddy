package com.timebuddy.services;

import com.timebuddy.dtos.UpdatePasswordDto;
import com.timebuddy.dtos.UserRequestDto;
import com.timebuddy.dtos.UserResponseDto;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 * Provides methods to register users, find users by ID, and load users by their username for authentication.
 */
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for UserService.
     *
     * @param userRepo The repository used to interact with the User database table.
     * @param passwordEncoder The password encoder used to hash and verify passwords.
     */
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users from the database and converts them to UserResponseDto objects.
     *
     * @return A list of UserResponseDto representing all users.
     */
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepo.findAll();
        // Konvertera varje User till UserDto och samla dem i en lista
        return users.stream()
                .map(user -> new UserResponseDto(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID and returns a UserResponseDto.
     *
     * @param id The ID of the user to be retrieved.
     * @return An Optional containing the UserResponseDto if found, otherwise an empty Optional.
     */
    public Optional<UserResponseDto> findUserById(Long id) {
        return userRepo.findById(id)
                .map(this::convertToResponseDto);
    }

    /**
     * Retrieves a user by their username and returns a UserResponseDto.
     *
     * @param username The username of the user to be retrieved.
     * @return An Optional containing the UserResponseDto if found, otherwise an empty Optional.
     */
    public Optional<UserResponseDto> loadUserByUsername(String username) {
        return userRepo.findByUsername(username)
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
        return userRepo.findById(id)
                .map(user -> {
                    // Update user fields
                    user.setUsername(updatedUser.getUsername());
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPassword(updatedUser.getPassword()); // Only update password if provided
                    }
                    user.setUpdatedAt(new java.util.Date()); // Set the updated timestamp
                    User updatedUserEntity = userRepo.save(user);
                    return convertToResponseDto(updatedUserEntity);
                });
    }

    /**
     * Updates the user's password.
     *
     * @param updatePasswordDto The DTO containing the username and new password.
     *                          The new password should be encrypted before calling this method.
     */


    public void updatePassword(String username, String encryptedPassword) {
        // Hämta användaren från databasen
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Uppdatera användarens lösenord
            user.setPassword(encryptedPassword);
            user.setUpdatedAt(new Date());

            // Spara uppdaterad användare
            userRepo.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }


    public Optional<UserRequestDto> setRoles(UserRequestDto userRequestDto) {
        // Hämta användaren som ska uppdateras
        Optional<User> userToUpdateOptional = userRepo.findByUsername(userRequestDto.getUsername());

        // Kontrollera om användaren existerar
        if (userToUpdateOptional.isEmpty()) {
            return Optional.empty();  // Returnera tom Optional om användaren inte hittas
        }

        // Extrahera användaren
        User userToUpdate = userToUpdateOptional.get();

        // Uppdatera användarens roller och det uppdaterade datumet
        userToUpdate.setRoles(userRequestDto.getAuthorities()); // Kontrollera att User har en setRoles-metod
        userToUpdate.setUpdatedAt(new Date());

        // Spara den uppdaterade användaren
        User updatedUser = userRepo.save(userToUpdate);

        // Returnera den uppdaterade användaren som UserRequestDto
        return Optional.of(new UserRequestDto(updatedUser.getUsername(), null, updatedUser.getRoles()));
    }



    /**
     * Deletes a user by their ID.
     *
     * @param username The ID of the user to be deleted.
     */
    public String deleteUser(String username) {
        // Hämta användaren som ska raderas
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            userRepo.delete(user.get());  // Ta bort användaren från databasen
        } else {
            throw new RuntimeException("User not found");  // Kasta ett undantag om användaren inte hittas
        }
        return "User deleted";  // Returnera framgångsmeddelande
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

    public String getPassword(String username) {
        return userRepo.findByUsername(username)
                .map(User::getPassword)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}



