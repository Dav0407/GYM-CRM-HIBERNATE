package com.epam.gym_crm.repository_test;

import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository_impl.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
    }

    @Test
    void save_NewUser_ShouldPersist() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");

        // Act
        User savedUser = userRepository.save(newUser);

        // Assert
        verify(entityManager).persist(newUser);
        assertEquals(newUser, savedUser);
    }

    @Test
    void save_ExistingUser_ShouldMerge() {
        // Arrange
        testUser.setPassword("newpassword");

        // Act
        when(entityManager.contains(testUser)).thenReturn(true);
        when(entityManager.merge(testUser)).thenReturn(testUser);
        User updatedUser = userRepository.save(testUser);

        // Assert
        verify(entityManager).merge(testUser);
        assertNotNull(updatedUser);
        assertEquals(testUser, updatedUser);
    }

    @Test
    void save_WithException_ShouldThrowRuntimeException() {
        // Arrange
        User newUser = new User();
        doThrow(new RuntimeException("Persistence error")).when(entityManager).persist(newUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userRepository.save(newUser));
    }

    @Test
    void findById_ExistingUser_ShouldReturnUser() {
        // Arrange
        when(entityManager.find(User.class, 1L)).thenReturn(testUser);

        // Act
        Optional<User> foundUser = userRepository.findById(1L);

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser, foundUser.get());
    }

    @Test
    void findById_NonExistingUser_ShouldReturnEmptyOptional() {
        // Arrange
        when(entityManager.find(User.class, 999L)).thenReturn(null);

        // Act
        Optional<User> foundUser = userRepository.findById(999L);

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findByUsername_ExistingUser_ShouldReturnUser() {
        // Arrange
        when(entityManager.find(User.class, "testuser")).thenReturn(testUser);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser, foundUser.get());
    }

    @Test
    void findByUsername_NonExistingUser_ShouldReturnEmptyOptional() {
        // Arrange
        when(entityManager.find(User.class, "nonexistent")).thenReturn(null);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void deleteByUsername_ExistingUser_ShouldRemove() {
        // Arrange
        when(entityManager.find(User.class, "testuser")).thenReturn(testUser);

        // Act
        userRepository.deleteByUsername("testuser");

        // Assert
        verify(entityManager).remove(testUser);
    }

    @Test
    void deleteByUsername_NonExistingUser_ShouldNotRemove() {
        // Arrange
        when(entityManager.find(User.class, "nonexistent")).thenReturn(null);

        // Act
        userRepository.deleteByUsername("nonexistent");

        // Assert
        verify(entityManager, never()).remove(any());
    }

    @Test
    void findAll_ShouldReturnUserList() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(testUser, new User());
        TypedQuery<User> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery("SELECT u FROM User u", User.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedUsers);

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertEquals(expectedUsers, users);
    }

    @Test
    void updatePassword_ExistingUser_ShouldUpdatePassword() {
        // Arrange
        TypedQuery<User> mockQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(testUser));

        // Act
        userRepository.updatePassword("testuser", "newpassword");

        // Assert
        verify(entityManager).merge(testUser);
        assertEquals("newpassword", testUser.getPassword());
    }

    @Test
    void updatePassword_NonExistingUser_ShouldThrowException() {
        // Arrange
        TypedQuery<User> mockQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userRepository.updatePassword("nonexistent", "newpassword"));
    }
}