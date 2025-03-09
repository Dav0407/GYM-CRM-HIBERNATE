package com.epam.gym_crm.service_test;

import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.UserRepository;
import com.epam.gym_crm.service.service_impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john.doe");
        user.setPassword("password123");
        user.setIsActive(true);
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals("john.doe", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCheckUsernameExists() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        boolean exists = userService.checkUsernameExists("john.doe");

        assertTrue(exists);
        verify(userRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    void testCheckUsernameDoesNotExist() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        boolean exists = userService.checkUsernameExists("nonexistent");

        assertFalse(exists);
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testIsPasswordValid() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        boolean isValid = userService.isPasswordValid("john.doe", "password123");

        assertTrue(isValid);
        verify(userRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    void testIsPasswordInvalid() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        boolean isValid = userService.isPasswordValid("john.doe", "wrongpassword");

        assertFalse(isValid);
        verify(userRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    void testChangePassword() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        userService.changePassword("john.doe", "password123", "newpassword");

        verify(userRepository, times(1)).updatePassword("john.doe", "newpassword");
    }

    @Test
    void testChangePasswordWithIncorrectOldPassword() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("john.doe", "wrongpassword", "newpassword");
        });

        verify(userRepository, never()).updatePassword(anyString(), anyString());
    }

    @Test
    void testGenerateUsername() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        String username = userService.generateUsername("John", "Doe");

        assertEquals("john.doe", username);
        verify(userRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    void testGenerateUsernameWithConflict() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("john.doe1")).thenReturn(Optional.empty());

        String username = userService.generateUsername("John", "Doe");

        assertEquals("john.doe1", username);
        verify(userRepository, times(1)).findByUsername("john.doe");
        verify(userRepository, times(1)).findByUsername("john.doe1");
    }

    @Test
    void testGenerateRandomPassword() {
        String password = userService.generateRandomPassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void testUpdateStatus() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        userService.updateStatus("john.doe");

        assertFalse(user.getIsActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        userService.deleteUser("john.doe");

        verify(userRepository, times(1)).deleteByUsername("john.doe");
    }

    @Test
    void testDeleteNonExistentUser() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser("nonexistent");
        });

        verify(userRepository, never()).deleteByUsername(anyString());
    }

    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername("john.doe");

        assertNotNull(foundUser);
        assertEquals("john.doe", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername("nonexistent");
        });

        verify(userRepository, times(1)).findByUsername("nonexistent");
    }
}