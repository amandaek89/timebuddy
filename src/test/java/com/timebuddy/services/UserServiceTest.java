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
    void testUpdateUser_UserFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Create a UserRequestDto with new username and password
        mockUserRequestDto.setUsername("updateduser");
        mockUserRequestDto.setPassword("updatedpassword");

        // Act
        Optional<UserResponseDto> result = userService.updateUser(1L, mockUserRequestDto);

        // Assert
        assertTrue(result.isPresent(), "User should be updated successfully");
        assertEquals("updateduser", result.get().getUsername(), "Updated username should match");
        assertEquals(1L, result.get().getId(), "User ID should match");
    }


    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<UserResponseDto> result = userService.updateUser(1L, mockUserRequestDto);

        // Assert
        assertFalse(result.isPresent(), "User should not be updated if not found");
    }

    @Test
    void testDeleteUser_UserExists() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotExists() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }
}


