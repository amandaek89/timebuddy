package com.timebuddy.controllers;

import com.timebuddy.dtos.AuthenticationRequest;
import com.timebuddy.dtos.ApiResponse;
import com.timebuddy.services.AuthenticationService;
import com.timebuddy.exceptions.UserAlreadyExistsException;
import com.timebuddy.exceptions.InvalidLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user authentication, including registration and login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Constructor to initialize the AuthenticationService.
     *
     * @param authenticationService The authentication service to handle user registration and login.
     */
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Endpoint for registering a new user.
     *
     * @param authRequest The authentication request containing the username and password.
     * @return A response indicating the result of the registration process.
     */
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
            String jwtToken = authenticationService.authenticate(authRequest);
            return new ApiResponse<>(HttpStatus.OK.value(), "Login successful", jwtToken);
        } catch (InvalidLoginException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }
}
