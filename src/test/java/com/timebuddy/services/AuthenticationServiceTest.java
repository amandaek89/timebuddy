package com.timebuddy.services;

import com.timebuddy.dtos.AuthenticationRequest;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import com.timebuddy.exceptions.UserAlreadyExistsException;
import com.timebuddy.exceptions.InvalidLoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AuthenticationService class.
 */
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepo;  // Mock of the user repository

    @Mock
    private JwtService jwtService;  // Mock of the JWT service

    @Mock
    private PasswordEncoder passwordEncoder;  // Mock of the password encoder

    @InjectMocks
    private AuthenticationService authenticationService;  // The service being tested

    private AuthenticationRequest validAuthRequest;
    private AuthenticationRequest invalidAuthRequest;
    private User testUser;

    /**
     * Initializes the test data before each test method.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks

        validAuthRequest = new AuthenticationRequest("validUser", "validPassword");
        invalidAuthRequest = new AuthenticationRequest("validUser", "invalidPassword");
        testUser = new User(1L, "validUser", "validPassword", new java.util.Date(), new java.util.Date(), null);
    }

    /**
     * Tests that a new user is registered successfully.
     */
    @Test
    void register_newUser_registersSuccessfully() {
        // Given
        when(userRepo.findByUsername("newUser")).thenReturn(Optional.empty());  // Mock user not found
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        AuthenticationRequest authRequest = new AuthenticationRequest("newUser", "password");

        // When
        String result = authenticationService.register(authRequest);

        // Then
        assertEquals("User registered successfully", result);  // Expect registration success
        verify(userRepo).save(any(User.class));  // Verify that the user is saved
    }

    /**
     * Tests that an exception is thrown if the user already exists.
     */
    @Test
    void register_existingUser_throwsException() {
        // Given: Mocka användaren direkt här i testmetoden
        User existingUser = new User(1L, "validUser", "validPassword", new java.util.Date(), new java.util.Date(), null);

        // Mocka att användaren med användarnamnet "validUser" redan finns i databasen
        when(userRepo.findByUsername("validUser")).thenReturn(Optional.of(existingUser));  // Simulera att användaren redan finns

        AuthenticationRequest authRequest = new AuthenticationRequest("validUser", "validPassword");

        // When & Then: Förvänta oss att UserAlreadyExistsException kastas när användaren redan finns
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationService.register(authRequest);
        });

        // Verifiera att rätt felmeddelande returneras i undantaget
        assertEquals("User already exists", exception.getMessage());

        // Verify: Kontrollera att save INTE anropas eftersom användaren redan finns
        verify(userRepo, never()).save(any(User.class));
    }


    /**
     * Tests that a JWT token is returned when valid credentials are provided.
     */

    @Test
    void authenticate_validCredentials_returnsToken() {
        AuthenticationRequest authRequest = new AuthenticationRequest("validUser", "validPassword");
        authRequest.setUsername("validUser");
        authRequest.setPassword("validPassword");

        User user = new User();
        user.setUsername("validUser");
        user.setPassword("encodedPassword");

        // Mocking repository and password check for valid user
        when(userRepo.findByUsername("validUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("validPassword", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token");

        // Authenticating the valid user
        String result = authenticationService.authenticate(authRequest);

        assertEquals("token", result);
        // Verifying that the token is generated
        verify(jwtService).generateToken(user);
    }


    /**
     * Tests that an exception is thrown when invalid credentials are provided.
     */
    @Test
    void authenticate_invalidCredentials_throwsException() {
        // Given
        when(userRepo.findByUsername("validUser")).thenReturn(Optional.of(testUser));  // Mock user found
        when(passwordEncoder.matches("invalidPassword", "encodedPassword")).thenReturn(false);  // Mock password mismatch

        // When & Then
        assertThrows(InvalidLoginException.class, () -> authenticationService.authenticate(invalidAuthRequest));  // Expect exception
    }
}
