package com.timebuddy.services;

import com.timebuddy.models.Role;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private Date now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Skapa ett mockat User-objekt
        now = new Date();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        mockUser.setCreatedAt(now);
        mockUser.setUpdatedAt(now);
        mockUser.setAuthorities(roles);
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void updatePassword_ShouldUpdatePassword_WhenUserExists() {
        // Arrange
        String newPassword = "newPassword";
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        // Act
        String result = userService.updatePassword(userDetails, newPassword);

        // Assert
        assertEquals("Password updated", result);
        assertEquals(newPassword, mockUser.getPassword());
        verify(userRepo, times(1)).save(mockUser);
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage_WhenUserExists() {
        // Arrange
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Act
        String result = userService.deleteUser("testuser");

        // Assert
        assertEquals("User deleted", result);
        verify(userRepo, times(1)).delete(mockUser);
    }
}
