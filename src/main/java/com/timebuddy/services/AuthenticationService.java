package com.timebuddy.services;

import com.timebuddy.dtos.AuthenticationRequest;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationService {


private final UserRepository userRepo;                  // Repository för att hantera användare i databasen
private final JwtService jwtService;              // Tjänst för att hantera JWT-token
private final PasswordEncoder passwordEncoder;    // Verktyg för att kryptera och kontrollera lösenord

/**
 * Konstruktor för att injicera nödvändiga beroenden.
 *
 * @param userRepo        Användarrepository för att hantera databasanrop.
 * @param jwtService      JWT-tjänst för att skapa och verifiera token.
 * @param passwordEncoder PasswordEncoder för att hantera lösenordskryptering.
 */
@Autowired
public AuthenticationService(UserRepository userRepo, JwtService jwtService, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
}

/**
 * Registrerar en ny användare baserat på en AuthRequest.
 *
 * @param authRequest Innehåller användarnamn och lösenord för den nya användaren.
 * @return En sträng som anger att användaren har registrerats, eller ett felmeddelande.
 */
public String register(AuthenticationRequest authRequest) {

    // Kontrollera om användarnamnet redan finns i databasen
    if (userRepo.findByUsername(authRequest.getUsername()).isPresent()) {
        return new RuntimeException("User already exists").getMessage();
    }

    // Skapa en ny användare
    User newUser = new User();
    newUser.setUsername(authRequest.getUsername());
    newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));  // Kryptera lösenordet

    newUser.setCreatedAt(new Date());   // Ange skapelsedatum
    newUser.setUpdatedAt(new Date());   // Ange senaste uppdateringsdatum

    userRepo.save(newUser);  // Spara den nya användaren i databasen

    return "User registered successfully";
}

/**
 * Autentiserar en användare baserat på en AuthRequest.
 *
 * @param authRequest Innehåller användarnamn och lösenord för autentisering.
 * @return En JWT-token om autentiseringen lyckas, annars ett felmeddelande.
 */
    /**
     * Authenticates the user by verifying the provided username and password.
     * If the username exists and the password matches, a JWT token is generated.
     *
     * @param authRequest The authentication request containing the username and password.
     * @return A JWT token if the authentication is successful.
     * @throws RuntimeException If the username is not found or the password is incorrect.
     */
    public String authenticate(AuthenticationRequest authRequest) {
        // Hämtar användaren från databasen baserat på användarnamnet
        Optional<User> user = userRepo.findByUsername(authRequest.getUsername());

        // Om användaren inte finns, kasta ett undantag
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Verifierar lösenordet
        if (passwordEncoder.matches(authRequest.getPassword(), user.get().getPassword())) {
            // Om lösenordet är korrekt, generera och returnera en JWT-token
            return jwtService.generateToken(user.get());
        }

        // Om lösenordet inte stämmer, kasta ett undantag
        throw new RuntimeException("Invalid login credentials");
    }


}
