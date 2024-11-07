package com.timebuddy.controllers;

import com.timebuddy.dtos.UserRequestDto;
import com.timebuddy.dtos.UserResponseDto;
import com.timebuddy.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for managing user data.
 * Handles user updates, fetching users by ID or username, and deletion of users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection for UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity with the user data or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
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
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        Optional<UserResponseDto> userResponseDto = userService.loadUserByUsername(username);
        return userResponseDto
                .map(ResponseEntity::ok)  // Return the user if found
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if not found
    }

    /**
     * Updates an existing user's information.
     *
     * @param id The ID of the user to update.
     * @param updatedUserDto The updated user data.
     * @return ResponseEntity with the updated user or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto updatedUserDto) {
        Optional<UserResponseDto> updatedUser = userService.updateUser(id, updatedUserDto);
        return updatedUser
                .map(ResponseEntity::ok)  // Return the updated user if successful
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if not found
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity with no content if deletion is successful.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}


