package com.epam.gym_crm.repository_impl;

import com.epam.gym_crm.entity.TrainingType;
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
public class TrainingTypeRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<TrainingType> typedQuery;

    @InjectMocks
    private TrainingTypeRepositoryImpl trainingTypeRepository;

    private TrainingType trainingType;

    @BeforeEach
    public void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Cardio");
    }

    @Test
    public void testSave_NewTrainingType_ShouldPersist() {
        // Arrange
        trainingType.setId(null);

        // Act
        TrainingType result = trainingTypeRepository.save(trainingType);

        // Assert
        verify(entityManager, times(1)).persist(trainingType);
        verify(entityManager, never()).merge(any(TrainingType.class));
        assertEquals(trainingType, result);
    }

    @Test
    public void testSave_ExistingTrainingType_ShouldMerge() {
        // Arrange
        trainingType.setId(1L);
        when(entityManager.merge(trainingType)).thenReturn(trainingType);

        // Act
        TrainingType result = trainingTypeRepository.save(trainingType);

        // Assert
        verify(entityManager, never()).persist(any(TrainingType.class));
        verify(entityManager, times(1)).merge(trainingType);
        assertEquals(trainingType, result);
    }

    @Test
    public void testSave_PersistException_ShouldThrowRuntimeException() {
        // Arrange
        trainingType.setId(null);
        doThrow(new RuntimeException("Database error")).when(entityManager).persist(any(TrainingType.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainingTypeRepository.save(trainingType);
        });

        assertTrue(exception.getMessage().contains("Failed to save TrainingType"));
    }

    @Test
    public void testSave_MergeException_ShouldThrowRuntimeException() {
        // Arrange
        trainingType.setId(1L);
        doThrow(new RuntimeException("Database error")).when(entityManager).merge(any(TrainingType.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            trainingTypeRepository.save(trainingType);
        });

        assertTrue(exception.getMessage().contains("Failed to save TrainingType"));
    }

    @Test
    public void testFindById_ExistingId_ShouldReturnTrainingType() {
        // Arrange
        Long id = 1L;
        when(entityManager.find(TrainingType.class, id)).thenReturn(trainingType);

        // Act
        Optional<TrainingType> result = trainingTypeRepository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainingType, result.get());
    }

    @Test
    public void testFindById_NonExistingId_ShouldReturnEmptyOptional() {
        // Arrange
        Long id = 999L;
        when(entityManager.find(TrainingType.class, id)).thenReturn(null);

        // Act
        Optional<TrainingType> result = trainingTypeRepository.findById(id);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByValue_ExistingValue_ShouldReturnTrainingType() {
        // Arrange
        String value = "Cardio";

        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyString())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(trainingType);

        // Act
        Optional<TrainingType> result = trainingTypeRepository.findByValue(value);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainingType, result.get());
        verify(typedQuery).setParameter("trainingType", value.toLowerCase());
    }

    @Test
    public void testFindByValue_NonExistingValue_ShouldReturnEmptyOptional() {
        // Arrange
        String value = "NonExistent";

        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), anyString())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        Optional<TrainingType> result = trainingTypeRepository.findByValue(value);

        // Assert
        assertTrue(result.isEmpty());
    }
}