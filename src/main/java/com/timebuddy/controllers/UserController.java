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


    @Operation(summary = "Uppdatera lösenord", description = "Uppdaterar ett lösenord för en specifik användare. Kräver nuvarande lösenord för att ändra till ett nytt.")
    @PutMapping
    public ResponseEntity<String> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,  // Hämta den autentiserade användaren
            @RequestBody UpdatePasswordDto changePasswordDto) {

        // Kontrollera om nuvarande lösenord matchar
        User user = userService.getUserByUsername(userDetails.getUsername());
        String encryptedPassword = user.getPassword();

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), encryptedPassword)) {
            return ResponseEntity.status(400).body("Current password is incorrect");
        }

        // Kontrollera om det nya lösenordet är detsamma som det nuvarande
        if (passwordEncoder.matches(changePasswordDto.getNewPassword(), encryptedPassword)) {
            return ResponseEntity.status(400).body("New password cannot be the same as the current password");
        }

        // Kryptera det nya lösenordet och uppdatera
        String newEncryptedPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());

        // Anropa service-metoden för att uppdatera lösenordet
        String response = userService.updatePassword(userDetails, newEncryptedPassword);

        if (response.equals("Password updated")) {
            return ResponseEntity.ok("Password updated");
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }

    @Operation(summary = "Radera användare", description = "Raderar den autentiserade användarens konto.")
    @DeleteMapping
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Hämta användarnamnet från den autentiserade användaren
        String username = userDetails.getUsername();

        // Anropa deleteUser från service-klassen för att radera användaren
        String response = userService.deleteUser(username);

        if ("User not found".equals(response)) {
            // Om användaren inte finns, returnera 404
            return ResponseEntity.status(404).body(response);
        }

        // Om användaren raderas, returnera ett framgångsmeddelande
        return ResponseEntity.status(200).body("User deleted");
    }
}
