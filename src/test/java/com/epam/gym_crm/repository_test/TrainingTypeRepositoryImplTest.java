package com.epam.gym_crm.repository_test;

import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.repository_impl.TrainingTypeRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TrainingTypeRepositoryImpl trainingTypeRepository;

    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Strength");
    }

    @Test
    void save_NewTrainingType_ShouldPersist() {
        // Arrange
        TrainingType newTrainingType = new TrainingType();
        newTrainingType.setTrainingTypeName("Cardio");

        // Act
        doAnswer(invocation -> {
            TrainingType argument = invocation.getArgument(0);
            argument.setId(2L); // Simulate ID assignment
            return argument;
        }).when(entityManager).persist(newTrainingType);

        TrainingType savedTrainingType = trainingTypeRepository.save(newTrainingType);

        // Assert
        verify(entityManager).persist(newTrainingType);
        assertNotNull(savedTrainingType);
        assertNotNull(savedTrainingType.getId());
        assertEquals("Cardio", savedTrainingType.getTrainingTypeName());
    }

    @Test
    void save_ExistingTrainingType_ShouldMerge() {
        // Arrange
        TrainingType existingTrainingType = new TrainingType();
        existingTrainingType.setId(1L);
        existingTrainingType.setTrainingTypeName("Updated Strength");

        // Act
        when(entityManager.merge(any(TrainingType.class))).thenReturn(existingTrainingType);
        TrainingType savedTrainingType = trainingTypeRepository.save(existingTrainingType);

        // Assert
        verify(entityManager).merge(existingTrainingType);
        assertNotNull(savedTrainingType);
        assertEquals("Updated Strength", savedTrainingType.getTrainingTypeName());
    }

    @Test
    void save_WhenExceptionOccurs_ShouldThrowRuntimeException() {
        // Arrange
        TrainingType trainingTypeToSave = new TrainingType();
        doThrow(new RuntimeException("Persistence error")).when(entityManager).persist(trainingTypeToSave);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> trainingTypeRepository.save(trainingTypeToSave));
    }

    @Test
    void findById_ExistingTrainingType_ShouldReturnOptional() {
        // Arrange
        Long trainingTypeId = 1L;
        when(entityManager.find(eq(TrainingType.class), eq(trainingTypeId))).thenReturn(trainingType);

        // Act
        Optional<TrainingType> foundTrainingType = trainingTypeRepository.findById(trainingTypeId);

        // Assert
        assertTrue(foundTrainingType.isPresent());
        assertEquals(trainingType, foundTrainingType.get());
    }

    @Test
    void findById_NonExistingTrainingType_ShouldReturnEmptyOptional() {
        // Arrange
        Long trainingTypeId = 999L;
        when(entityManager.find(eq(TrainingType.class), eq(trainingTypeId))).thenReturn(null);

        // Act
        Optional<TrainingType> foundTrainingType = trainingTypeRepository.findById(trainingTypeId);

        // Assert
        assertTrue(foundTrainingType.isEmpty());
    }

    @Test
    void findByValue_ExistingTrainingType_ShouldReturnOptional() {
        // Arrange
        String trainingTypeName = "Strength";
        TypedQuery<TrainingType> mockedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(
                "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :trainingType",
                TrainingType.class)
        ).thenReturn(mockedQuery);

        when(mockedQuery.setParameter("trainingType", trainingTypeName))
                .thenReturn(mockedQuery);

        when(mockedQuery.getSingleResult()).thenReturn(trainingType);

        // Act
        Optional<TrainingType> foundTrainingType = trainingTypeRepository.findByValue(trainingTypeName);

        // Assert
        assertTrue(foundTrainingType.isPresent());
        assertEquals(trainingType, foundTrainingType.get());
        verify(mockedQuery).setParameter("trainingType", trainingTypeName);
    }

    @Test
    void findByValue_NonExistingTrainingType_ShouldReturnEmptyOptional() {
        // Arrange
        String trainingTypeName = "NonExistent";
        TypedQuery<TrainingType> mockedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(
                "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :trainingType",
                TrainingType.class)
        ).thenReturn(mockedQuery);

        when(mockedQuery.setParameter("trainingType", trainingTypeName))
                .thenReturn(mockedQuery);

        when(mockedQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        Optional<TrainingType> foundTrainingType = trainingTypeRepository.findByValue(trainingTypeName);

        // Assert
        assertTrue(foundTrainingType.isEmpty());
        verify(mockedQuery).setParameter("trainingType", trainingTypeName);
    }
}