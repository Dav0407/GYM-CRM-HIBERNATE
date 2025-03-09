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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> typedQuery;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password123");
    }

    @Test
    public void testSave_NewUser_ShouldPersist() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("newPassword");

        // Act
        User result = userRepository.save(newUser);

        // Assert
        verify(entityManager).persist(newUser);
        verify(entityManager, never()).merge(any(User.class));
        assertSame(newUser, result);
    }

    @Test
    public void testSave_ExistingUser_ShouldMerge() {
        // Arrange
        when(entityManager.merge(user)).thenReturn(user);

        // Act
        User result = userRepository.save(user);

        // Assert
        verify(entityManager).merge(user);
        verify(entityManager, never()).persist(any(User.class));
        assertSame(user, result);
    }

    @Test
    public void testSave_PersistException_ShouldThrowRuntimeException() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("newPassword");

        // Mock entityManager.persist() to throw an exception
        doThrow(new RuntimeException("Database error")).when(entityManager).persist(any(User.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userRepository.save(newUser));

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Failed to save user"));
    }

    @Test
    public void testFindById_ExistingId_ShouldReturnUser() {
        // Arrange
        Long id = 1L;
        when(entityManager.find(User.class, id)).thenReturn(user);

        // Act
        Optional<User> result = userRepository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    public void testFindById_NonExistingId_ShouldReturnEmptyOptional() {
        // Arrange
        Long id = 999L;
        when(entityManager.find(User.class, id)).thenReturn(null);

        // Act
        Optional<User> result = userRepository.findById(id);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByUsername_ExistingUsername_ShouldReturnUser() {
        // Arrange
        String username = "testUser";

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(user));

        // Act
        Optional<User> result = userRepository.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    public void testFindByUsername_NonExistingUsername_ShouldReturnEmptyOptional() {
        // Arrange
        String username = "nonExistentUser";

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        Optional<User> result = userRepository.findByUsername(username);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteByUsername_ExistingUsername_ShouldRemoveUser() {
        // Arrange
        String username = "testUser";

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(user));

        // Act
        userRepository.deleteByUsername(username);

        // Assert
        verify(entityManager).remove(user);
    }

    @Test
    public void testDeleteByUsername_NonExistingUsername_ShouldDoNothing() {
        // Arrange
        String username = "nonExistentUser";

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        userRepository.deleteByUsername(username);

        // Assert
        verify(entityManager, never()).remove(any(User.class));
    }

    @Test
    public void testFindAll_ShouldReturnAllUsers() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testUser2");

        List<User> userList = List.of(user, user2);

        when(entityManager.createQuery("SELECT u FROM User u", User.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(userList);

        // Act
        List<User> result = userRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(userList, result);
    }

    @Test
    public void testUpdatePassword_ExistingUsername_ShouldUpdatePassword() {
        // Arrange
        String username = "testUser";
        String newPassword = "newPassword123";

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(user));

        // Act
        userRepository.updatePassword(username, newPassword);

        // Assert
        assertEquals(newPassword, user.getPassword());
        verify(entityManager).merge(user);
    }

    @Test
    public void testUpdatePassword_NonExistingUsername_ShouldThrowException() {
        // Arrange
        String username = "nonExistentUser";
        String newPassword = "newPassword123";

        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("username", username)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userRepository.updatePassword(username, newPassword));

        assertTrue(exception.getMessage().contains("User not found with username"));
        verify(entityManager, never()).merge(any(User.class));
    }
}