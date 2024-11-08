package com.timebuddy.services;

import com.timebuddy.dtos.AuthenticationRequest;
import com.timebuddy.models.Role;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import com.timebuddy.exceptions.UserAlreadyExistsException;
import com.timebuddy.exceptions.InvalidLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationService {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor to initialize dependencies for the service.
     *
     * @param userRepo        The user repository to handle user data.
     * @param jwtService      The service to generate JWT tokens.
     * @param passwordEncoder The password encoder to securely handle passwords.
     */
    @Autowired
    public AuthenticationService(UserRepository userRepo, @Lazy JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user. If the username already exists, a UserAlreadyExistsException is thrown.
     *
     * @param authRequest The request containing the username and password.
     * @return A message indicating the result of the registration process.
     * @throws UserAlreadyExistsException If the username already exists in the system.
     */
    public String register(AuthenticationRequest authRequest) throws UserAlreadyExistsException {
        // Check if the username already exists in the database
        if (userRepo.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // Create a new user and encode the password
        User newUser = new User();
        newUser.setUsername(authRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));  // Encrypt the password

        // Assign the default role 'ROLE_USER' to the user
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        // Set creation and update timestamps
        newUser.setCreatedAt(new Date());
        newUser.setUpdatedAt(new Date());

        // Save the new user to the database
        userRepo.save(newUser);

        return "User registered successfully";
    }

    /**
     * Authenticates a user by verifying the provided username and password.
     * If the username does not exist or the password is incorrect, an exception is thrown.
     *
     * @param authRequest The request containing the username and password.
     * @return A JWT token if authentication is successful.
     * @throws InvalidLoginException If the username is not found or the password is incorrect.
     */
    public String authenticate(AuthenticationRequest authRequest) throws InvalidLoginException {
        // Fetch the user from the database based on the username
        Optional<User> userOptional = userRepo.findByUsername(authRequest.getUsername());

        // If the user doesn't exist, throw an exception
        if (userOptional.isEmpty()) {
            throw new InvalidLoginException("User not found");
        }

        User user = userOptional.get();
        // Verify the password
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new InvalidLoginException("Invalid login credentials");
        }

        // If the password is correct, generate and return a JWT token
        return jwtService.generateToken(user);
    }
}

