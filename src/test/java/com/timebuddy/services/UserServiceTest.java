package com.timebuddy.services;

import com.timebuddy.dtos.UserDto;
import com.timebuddy.models.Role;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByUsername_shouldReturnUserWhenFound() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserByUsername_shouldThrowExceptionWhenNotFound() {
        // Arrange
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserByUsername("unknown"));
        assertEquals("User not found with username: unknown", exception.getMessage());
    }

    @Test
    void updatePassword_shouldUpdatePasswordWhenUserFound() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        String result = userService.updatePassword("testuser", "newPassword");

        // Assert
        assertEquals("Password updated", result);
        verify(userRepo).save(user);
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void updatePassword_shouldReturnErrorWhenUserNotFound() {
        // Arrange
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act
        String result = userService.updatePassword("unknown", "newPassword");

        // Assert
        assertEquals("User not found", result);
        verify(userRepo, never()).save(any());
    }


    @Test
    void getAllUsers_shouldReturnUserDtos() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setAuthorities(Set.of(Role.valueOf("ROLE_USER")));

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setAuthorities(Set.of(Role.valueOf("ROLE_ADMIN")));

        when(userRepo.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserDto> users = userService.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getUsername());
        assertIterableEquals(Set.of("ROLE_USER"), users.get(0).getAuthorities());
        assertEquals("user2", users.get(1).getUsername());
        assertIterableEquals(Set.of("ROLE_ADMIN"), users.get(1).getAuthorities());
    }

    @Test
    void setRoles_shouldUpdateRolesWhenUserFound() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setAuthorities(Set.of(Role.valueOf("ROLE_USER")));

        UserDto userDto = new UserDto(1L, "testuser", Set.of(Role.valueOf("ROLE_ADMIN")));

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user); // Mockar att `save` returnerar användaren

        // Act
        Optional<UserDto> result = userService.setRoles(userDto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertIterableEquals(Set.of("ROLE_ADMIN"), result.get().getAuthorities());
        verify(userRepo).save(user); // Verifierar att `save` anropas
    }

    @Test
    void setRoles_shouldReturnEmptyWhenUserNotFound() {
        // Arrange
        UserDto userDto = new UserDto(1L, "unknown", Set.of(Role.valueOf("ROLE_ADMIN")));

        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act
        Optional<UserDto> result = userService.setRoles(userDto);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepo, never()).save(any());
    }

    @Test
    void deleteUser_shouldDeleteUserWhenFound() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        String result = userService.deleteUser("testuser");

        // Assert
        assertEquals("User deleted", result);
        verify(userRepo).delete(user);
    }

    @Test
    void deleteUser_shouldReturnErrorWhenUserNotFound() {
        // Arrange
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act
        String result = userService.deleteUser("unknown");

        // Assert
        assertEquals("User not found", result);
        verify(userRepo, never()).delete(any());
    }

    @Test
    void getPassword_shouldReturnPasswordWhenUserFound() {
        // Arrange
        User user = new User();
        user.setPassword("encryptedPassword");

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        String result = userService.getPassword("testuser");

        // Assert
        assertEquals("encryptedPassword", result);
    }

    @Test
    void getPassword_shouldReturnErrorWhenUserNotFound() {
        // Arrange
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act
        String result = userService.getPassword("unknown");

        // Assert
        assertEquals("User not found", result);
    }
}
