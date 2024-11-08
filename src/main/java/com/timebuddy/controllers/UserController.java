package com.timebuddy.controllers;

import com.timebuddy.dtos.UpdatePasswordDto;
import com.timebuddy.dtos.UserRequestDto;
import com.timebuddy.dtos.UserResponseDto;
import com.timebuddy.models.User;
import com.timebuddy.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing user data.
 * Handles user updates, fetching users by ID or username, and deletion of users.
 */
@Tag(name = "User Management", description = "API for managing user data.")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection for UserService
    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity with the user data or 404 if not found.
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id) {

    Optional<UserResponseDto> userResponseDto = userService.findUserById(id);
        return userResponseDto
                .map(ResponseEntity::ok)  // Return the user if found
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if not found
    }


    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return ResponseEntity with the user data or 404 if not found.
     */
    @Operation(summary = "Get user by username", description = "Retrieves a specific user by their username.")
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(
            @PathVariable String username) {

        Optional<UserResponseDto> userResponseDto = userService.loadUserByUsername(username);
        return userResponseDto
                .map(ResponseEntity::ok)  // Return the user if found
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if not found
    }

    /**
     * Retrieves all users.
     * @return ResponseEntity with a list of all users or 404 if no users are found.
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all users.")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        if (userService.getAllUsers().isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }
    /**
     * Updates an existing user's information.
     *
     * @param id The ID of the user to update.
     * @param updatedUserDto The updated user data.
     * @return ResponseEntity with the updated user or 404 if not found.
     */
    @Operation(summary = "Update user", description = "Updates an existing user's information.")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDto updatedUserDto) {

        Optional<UserResponseDto> updatedUser = userService.updateUser(id, updatedUserDto);
        return updatedUser
                .map(ResponseEntity::ok)  // Return the updated user if successful
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if not found
    }

    /**
        * Updates a user's password.
        *
     * @param updatePasswordDto The ChangePasswordDto containing the user's username, current password, and new password.
     *                          The current password must match the user's current password in the database.
     *                          The new password must be different from the current password.
     *                          The new password will be encrypted before saving.
     */
    @Operation(summary = "Update password", description = "Updates a user's password.")
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        // Kontrollera om användaren existerar
        Optional<UserResponseDto> userOptional = userService.loadUserByUsername(updatePasswordDto.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Hämta det befintliga krypterade lösenordet
        String currentEncryptedPassword = userService.getPassword(updatePasswordDto.getUsername());

        // Validera att det aktuella lösenordet är korrekt
        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), currentEncryptedPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
        }

        // Kontrollera att det nya lösenordet inte är samma som det gamla
        if (passwordEncoder.matches(updatePasswordDto.getNewPassword(), currentEncryptedPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password cannot be the same as the current password");
        }

        // Kryptera det nya lösenordet
        String encryptedNewPassword = passwordEncoder.encode(updatePasswordDto.getNewPassword());

        // Uppdatera lösenordet i databasen
        userService.updatePassword(updatePasswordDto.getUsername(), encryptedNewPassword);

        return ResponseEntity.ok("Password updated successfully");
    }

    /**
     * Deletes a user based on the username.
     *
     * @param username The username of the user to be deleted.
     * @return ResponseEntity with a success message or an error message if the user is not found.
     */
    @Operation(summary = "Delete user", description = "Deletes a user.")
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestParam String username) {
        try {
            String message = userService.deleteUser(username); // Call the service to delete the user
            return ResponseEntity.ok(message); // Return success message with 200 status
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage()); // Return error message with 404 status
        }
    }
}


