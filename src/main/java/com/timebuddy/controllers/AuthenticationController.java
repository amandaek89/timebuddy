package com.timebuddy.controllers;

import com.timebuddy.dtos.AuthenticationRequest;
import com.timebuddy.dtos.ApiResponse;
import com.timebuddy.services.AuthenticationService;
import com.timebuddy.exceptions.UserAlreadyExistsException;
import com.timebuddy.exceptions.InvalidLoginException;
import com.timebuddy.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user authentication, including registration and login.
 */
@Tag(name = "Authentication", description = "Endpoints for user authentication")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Constructor to initialize the AuthenticationService.
     *
     * @param authenticationService The authentication service to handle user registration and login.
     * @param userService           The user service to handle user-related operations.
     */
    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    /**
     * Endpoint for registering a new user.
     *
     * @param authRequest The authentication request containing the username and password.
     * @return A response indicating the result of the registration process.
     */
    @Operation(summary = "Register new user", description = "Registers a new user in the system with a username and password.")
    @PostMapping("/register")
    public ApiResponse<String> registerUser(@RequestBody AuthenticationRequest authRequest) {
        try {
            String responseMessage = authenticationService.register(authRequest);
            return new ApiResponse<>(HttpStatus.CREATED.value(), responseMessage, null);
        } catch (UserAlreadyExistsException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    /**
     * Endpoint for logging in an existing user and generating a JWT token.
     *
     * @param authRequest The authentication request containing the username and password.
     * @return A response containing the JWT token if the login is successful.
     */
    @PostMapping("/login")
    public ApiResponse<String> loginUser(@RequestBody AuthenticationRequest authRequest) {
        try {
            // Authenticates the user and retrieves the JWT token
            String jwtToken = authenticationService.authenticate(authRequest);

            // Return a successful response with the token
            return new ApiResponse<>(HttpStatus.OK.value(), "Login successful", jwtToken);
        } catch (InvalidLoginException e) {
            // Return an unauthorized response if login fails
            return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Authenticated");
    }


}
