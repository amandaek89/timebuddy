package com.timebuddy.controllers;

import com.timebuddy.dtos.UpdatePasswordDto;
import com.timebuddy.dtos.UserDto;
import com.timebuddy.dtos.UserRequestDto;
import com.timebuddy.dtos.UserResponseDto;
import com.timebuddy.models.User;
import com.timebuddy.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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


    @Operation(summary = "Hämta användare", description = "Hämtar en specifik användare baserat på användarnamn.")
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);  // Hämta användaren via tjänsten

        if (user == null) {
            return ResponseEntity.status(404).build();  // Returnera 404 om användaren inte finns
        }

        // Skapa en DTO av användaren
        UserResponseDto response = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream().map(role -> role.getAuthority()).collect(Collectors.toList()) // Mappar roller till deras namn
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Hämta alla användare", description = "Returnerar en lista med alla användare.")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        if (userService.getAllUsers().isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Uppdatera lösenord", description = "Uppdaterar ett lösenord för en specifik användare. Kräver nuvarande lösenord för att ändra till ett nytt.")
    @PutMapping
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDto changePasswordDto) {
        // Hämta användaren baserat på användarnamn
        User user = userService.getUserByUsername(changePasswordDto.getUsername());

        if (user == null) {  // Om användaren inte finns
            return ResponseEntity.status(404).body("User not found");
        }

        // Hämta det krypterade lösenordet för användaren
        String encryptedPassword = user.getPassword();

        // Kontrollera om det nuvarande lösenordet matchar
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), encryptedPassword)) {
            return ResponseEntity.status(400).body("Current password is incorrect");
        }

        // Kontrollera om det nya lösenordet är detsamma som det nuvarande
        if (passwordEncoder.matches(changePasswordDto.getNewPassword(), encryptedPassword)) {
            return ResponseEntity.status(400).body("New password cannot be the same as the current password");
        }

        // Kryptera det nya lösenordet
        String newEncryptedPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());

        // Uppdatera användarens lösenord i databasen
        userService.updatePassword(changePasswordDto.getUsername(), newEncryptedPassword);

        return ResponseEntity.ok("Password updated");
    }


    @Operation(summary = "Radera användare", description = "Raderar en specifik användare baserat på användarnamn.")
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        String response = userService.deleteUser(username);
        if (userService.deleteUser(username).equals(response)) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok("User deleted");
    }

    @Operation(summary = "Sätt roller", description = "Sätter roller för en användare. Användaren kan inte ändra sina egna roller.")
    @PutMapping("/roles")
    public ResponseEntity<UserDto> setRoles(@RequestBody UserDto userDto, @AuthenticationPrincipal UserDetails userDetails) {
        String loggedInUsername = userDetails.getUsername();

        if (userDto.getUsername().equals(loggedInUsername)) {
            return ResponseEntity.status(400).build();
        }

        return ResponseEntity.ok(userService.setRoles(userDto).get());
    }
}
