package com.timebuddy.services;

import com.timebuddy.dtos.UpdatePasswordDto;
import com.timebuddy.dtos.UserDto;
import com.timebuddy.dtos.UserRequestDto;
import com.timebuddy.dtos.UserResponseDto;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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
     * @param userRepo        The repository used to interact with the User database table.
     * @param passwordEncoder The password encoder used to hash and verify passwords.
     */
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Hämtar alla användare från databasen och konverterar dem till UserDto-objekt.
     *
     * @return En lista av UserDto som representerar alla användare.
     */
    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getAuthorities()))
                .collect(Collectors.toList());
    }

    /**
     * Hämtar en användare baserat på användarnamnet och returnerar den som UserDto.
     *
     * @param username Användarnamnet för användaren som ska hämtas.
     * @return En Optional som innehåller UserDto om användaren hittas, annars tomt.
     */
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }


    /**
     * Uppdaterar lösenordet för den autentiserade användaren.
     *
     * @param userDetails          Den autentiserade användarens detaljer (via @AuthenticationPrincipal).
     * @param newEncryptedPassword Det nya krypterade lösenordet.
     * @return Ett meddelande som indikerar resultatet.
     */
    public String updatePassword(UserDetails userDetails, String newEncryptedPassword) {
        String username = userDetails.getUsername();  // Hämta användarnamnet från den autentiserade användaren

        return userRepo.findByUsername(username)
                .map(user -> {
                    user.setPassword(newEncryptedPassword);  // Uppdatera lösenordet
                    user.setUpdatedAt(new Date());  // Uppdatera tidsstämpeln
                    userRepo.save(user);  // Spara användaren med det nya lösenordet
                    return "Password updated";
                })
                .orElse("User not found");  // Om användaren inte finns
    }


    /**
     * Uppdaterar rollerna för en användare om användaren hittas.
     *
     * @param userDto DTO som innehåller användarens nya roller.
     * @return En Optional som innehåller den uppdaterade UserDto, eller tomt om användaren inte hittas.
     */
    public Optional<UserDto> setRoles(UserDto userDto) {
        return userRepo.findByUsername(userDto.getUsername())
                .map(user -> {
                    user.setAuthorities(userDto.getAuthorities());
                    user.setUpdatedAt(new Date());
                    User updatedUser = userRepo.save(user);
                    return new UserDto(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getAuthorities());
                });
    }

    /**
     * Raderar en användare från databasen baserat på användarnamn.
     *
     * @param username Användarnamnet för den användare som ska raderas.
     * @return Ett meddelande som indikerar resultatet.
     */
    public String deleteUser(String username) {
        // Hitta användaren med hjälp av användarnamnet
        return userRepo.findByUsername(username)
                .map(user -> {
                    // Om användaren finns, radera användaren
                    userRepo.delete(user);
                    return "User deleted"; // Meddelande som bekräftar att användaren har raderats
                })
                .orElse("User not found"); // Om användaren inte finns, returnera "User not found"
    }

    /**
     * Hämtar lösenordet för en användare baserat på användarnamn.
     *
     * @param username Användarnamnet för användaren vars lösenord ska hämtas.
     * @return En sträng som innehåller användarens lösenord eller ett felmeddelande.
     */
    public String getPassword(String username) {
        return userRepo.findByUsername(username)
                .map(User::getPassword)
                .orElse("User not found");
    }
}