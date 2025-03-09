package com.epam.gym_crm.repository_test;

import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository_impl.TraineeRepositoryImpl;
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
public class TraineeRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Trainee> query;

    @InjectMocks
    private TraineeRepositoryImpl traineeRepository;

    private Trainee trainee;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
    }

    @Test
    public void testSave_NewTrainee_ShouldPersist() {
        // Arrange
        Trainee newTrainee = new Trainee();
        newTrainee.setUser(user);

        // Act
        Trainee savedTrainee = traineeRepository.save(newTrainee);

        // Assert
        verify(entityManager).persist(newTrainee);
        verify(entityManager, never()).merge(any(Trainee.class));
        assertSame(newTrainee, savedTrainee);
    }

    @Test
    public void testSave_ExistingTrainee_ShouldMerge() {
        // Arrange
        when(entityManager.merge(trainee)).thenReturn(trainee);

        // Act
        Trainee savedTrainee = traineeRepository.save(trainee);

        // Assert
        verify(entityManager).merge(trainee);
        verify(entityManager, never()).persist(any(Trainee.class));
        assertSame(trainee, savedTrainee);
    }

    @Test
    public void testSave_PersistException_ShouldThrowRuntimeException() {
        // Arrange
        Trainee newTrainee = new Trainee();
        newTrainee.setUser(user);

        doThrow(new RuntimeException("Database error")).when(entityManager).persist(any(Trainee.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> traineeRepository.save(newTrainee));

        assertTrue(exception.getMessage().contains("Failed to save trainee"));
    }

    @Test
    public void testFindById_ExistingTrainee_ShouldReturnTrainee() {
        // Arrange
        Long traineeId = 1L;
        when(entityManager.find(Trainee.class, traineeId)).thenReturn(trainee);

        // Act
        Optional<Trainee> result = traineeRepository.findById(traineeId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    public void testFindById_NonExistingTrainee_ShouldReturnEmptyOptional() {
        // Arrange
        Long traineeId = 999L;
        when(entityManager.find(Trainee.class, traineeId)).thenReturn(null);

        // Act
        Optional<Trainee> result = traineeRepository.findById(traineeId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByUserId_ExistingTrainee_ShouldReturnTrainee() {
        // Arrange
        Long userId = 1L;
        List<Trainee> traineeList = Collections.singletonList(trainee);

        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.getResultList()).thenReturn(traineeList);

        // Act
        Optional<Trainee> result = traineeRepository.findByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    public void testFindByUserId_NonExistingTrainee_ShouldReturnEmptyOptional() {
        // Arrange
        Long userId = 1L;
        List<Trainee> emptyList = Collections.emptyList();

        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.getResultList()).thenReturn(emptyList);

        // Act
        Optional<Trainee> result = traineeRepository.findByUserId(userId);

        // Assert
        assertFalse(result.isPresent());
    }
}