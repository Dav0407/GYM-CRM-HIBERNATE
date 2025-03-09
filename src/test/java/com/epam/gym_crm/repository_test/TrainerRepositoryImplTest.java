package com.epam.gym_crm.repository_test;

import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository_impl.TrainerRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Trainer> query;

    @InjectMocks
    private TrainerRepositoryImpl trainerRepository;

    private Trainer trainer;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");

        trainer = new Trainer();
        trainer.setUser(user);
    }

    @Test
    public void testSaveNewTrainer() {
        // Arrange
        trainer.setId(null);

        // Act
        Trainer savedTrainer = trainerRepository.save(trainer);

        // Assert
        verify(entityManager, times(1)).persist(trainer);
        verify(entityManager, never()).merge(any(Trainer.class));
        assertEquals(trainer, savedTrainer);
    }

    @Test
    public void testSaveExistingTrainer() {
        // Arrange
        trainer.setId(1L);
        when(entityManager.merge(trainer)).thenReturn(trainer);

        // Act
        Trainer savedTrainer = trainerRepository.save(trainer);

        // Assert
        verify(entityManager, never()).persist(any(Trainer.class));
        verify(entityManager, times(1)).merge(trainer);
        assertEquals(trainer, savedTrainer);
    }

    @Test
    public void testSaveWithException() {
        // Arrange
        trainer.setId(null);
        doThrow(new RuntimeException("Database error")).when(entityManager).persist(any(Trainer.class));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            trainerRepository.save(trainer);
        });

        assertTrue(exception.getMessage().contains("Failed to save Trainer"));
    }

    @Test
    public void testFindByIdWhenTrainerExists() {
        // Arrange
        Long trainerId = 1L;
        when(entityManager.find(Trainer.class, trainerId)).thenReturn(trainer);

        // Act
        Optional<Trainer> result = trainerRepository.findById(trainerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    public void testFindByIdWhenTrainerDoesNotExist() {
        // Arrange
        Long trainerId = 1L;
        when(entityManager.find(Trainer.class, trainerId)).thenReturn(null);

        // Act
        Optional<Trainer> result = trainerRepository.findById(trainerId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindAll() {
        // Arrange
        List<Trainer> trainerList = new ArrayList<>();
        trainerList.add(trainer);

        when(entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(trainerList);

        // Act
        List<Trainer> result = trainerRepository.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(trainer, result.get(0));
    }

    @Test
    public void testFindByUserIdWhenTrainerExists() {
        // Arrange
        Long userId = 1L;
        List<Trainer> trainerList = new ArrayList<>();
        trainerList.add(trainer);

        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.getResultList()).thenReturn(trainerList);

        // Act
        Optional<Trainer> result = trainerRepository.findByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    public void testFindByUserIdWhenTrainerDoesNotExist() {
        // Arrange
        Long userId = 1L;
        List<Trainer> emptyList = new ArrayList<>();

        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter("userId", userId)).thenReturn(query);
        when(query.getResultList()).thenReturn(emptyList);

        // Act
        Optional<Trainer> result = trainerRepository.findByUserId(userId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindUnassignedTrainersByTraineeUsername() {
        // Arrange
        String traineeUsername = "trainee.user";
        List<Trainer> unassignedTrainers = new ArrayList<>();
        unassignedTrainers.add(trainer);

        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter("traineeUsername", traineeUsername)).thenReturn(query);
        when(query.getResultList()).thenReturn(unassignedTrainers);

        // Act
        List<Trainer> result = trainerRepository.findUnassignedTrainersByTraineeUsername(traineeUsername);

        // Assert
        assertEquals(1, result.size());
        assertSame(trainer, result.get(0));
        verify(query).setParameter("traineeUsername", traineeUsername);
    }

    @Test
    public void testFindUnassignedTrainersByTraineeUsernameNoResults() {
        // Arrange
        String traineeUsername = "trainee.user";
        List<Trainer> emptyList = new ArrayList<>();

        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter("traineeUsername", traineeUsername)).thenReturn(query);
        when(query.getResultList()).thenReturn(emptyList);

        // Act
        List<Trainer> result = trainerRepository.findUnassignedTrainersByTraineeUsername(traineeUsername);

        // Assert
        assertTrue(result.isEmpty());
        verify(query).setParameter("traineeUsername", traineeUsername);
    }
}