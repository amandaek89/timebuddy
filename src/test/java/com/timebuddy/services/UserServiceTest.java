package com.timebuddy.services;

import com.timebuddy.dtos.UserRequestDto;
import com.timebuddy.dtos.UserResponseDto;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UserRequestDto mockUserRequestDto;

    @BeforeEach
    void setUp() {
        // Create a mock user entity
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("testpassword");

        // Create a mock UserRequestDto
        mockUserRequestDto = new UserRequestDto();
        mockUserRequestDto.setUsername("updateduser");
        mockUserRequestDto.setPassword("updatedpassword");
    }

    @Test
    void testFindUserById_UserFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<UserResponseDto> result = userService.findUserById(1L);

        // Assert
        assertTrue(result.isPresent(), "User should be found");
        assertEquals("testuser", result.get().getUsername(), "Username should match");
        assertEquals(1L, result.get().getId(), "User ID should match");
    }

    @Test
    void testFindUserById_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponseDto> result = userService.findUserById(1L);

        // Assert
        assertFalse(result.isPresent(), "User should not be found");
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Act
        Optional<UserResponseDto> result = userService.loadUserByUsername("testuser");

        // Assert
        assertTrue(result.isPresent(), "User should be found by username");
        assertEquals("testuser", result.get().getUsername(), "Username should match");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Act
        Optional<UserResponseDto> result = userService.loadUserByUsername("nonexistentuser");

        // Assert
        assertFalse(result.isPresent(), "User should not be found");
    }

    @Test
    void testDeleteUser_UserExists() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(mockUser);

        // Act
        String result = userService.deleteUser(username);

        // Assert
        assertEquals("User deleted", result, "Success message should match");
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(username));
        assertEquals("User not found", exception.getMessage(), "Exception message should match");
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).delete(any(User.class));
    }
}
