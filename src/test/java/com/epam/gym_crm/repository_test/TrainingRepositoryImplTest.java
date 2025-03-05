package com.epam.gym_crm.repository_test;

import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository_impl.TrainingRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TrainingRepositoryImpl trainingRepository;

    private Training training;

    @BeforeEach
    void setUp() {
        // Create test data
        User user = new User();
        user.setUsername("testUser");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Strength Training");

        training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingDate(new Date());
    }

    @Test
    void save_NewTraining_ShouldPersist() {
        // Arrange
        Training newTraining = new Training();
        newTraining.setId(null);

        // Act
        doAnswer(invocation -> {
            Training argument = invocation.getArgument(0);
            argument.setId(1L); // Simulate ID assignment
            return argument;
        }).when(entityManager).persist(newTraining);

        Training savedTraining = trainingRepository.save(newTraining);

        // Assert
        verify(entityManager).persist(newTraining);
        assertNotNull(savedTraining);
        assertNotNull(savedTraining.getId());
    }

    @Test
    void save_ExistingTraining_ShouldMerge() {
        // Arrange
        Training existingTraining = new Training();
        existingTraining.setId(1L);

        // Act
        when(entityManager.merge(any(Training.class))).thenReturn(existingTraining);
        Training savedTraining = trainingRepository.save(existingTraining);

        // Assert
        verify(entityManager).merge(existingTraining);
        assertNotNull(savedTraining);
    }

    @Test
    void save_WhenExceptionOccurs_ShouldThrowRuntimeException() {
        // Arrange
        Training trainingToSave = new Training();
        doThrow(new RuntimeException("Persistence error")).when(entityManager).persist(trainingToSave);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> trainingRepository.save(trainingToSave));
    }

    @Test
    void findById_ExistingTraining_ShouldReturnOptional() {
        // Arrange
        Long trainingId = 1L;
        when(entityManager.find(eq(Training.class), eq(trainingId))).thenReturn(training);

        // Act
        Optional<Training> foundTraining = trainingRepository.findById(trainingId);

        // Assert
        assertTrue(foundTraining.isPresent());
        assertEquals(training, foundTraining.get());
    }

    @Test
    void findById_NonExistingTraining_ShouldReturnEmptyOptional() {
        // Arrange
        Long trainingId = 999L;
        when(entityManager.find(eq(Training.class), eq(trainingId))).thenReturn(null);

        // Act
        Optional<Training> foundTraining = trainingRepository.findById(trainingId);

        // Assert
        assertTrue(foundTraining.isEmpty());
    }

    @Test
    void findAllTraineeTrainings_WithAllParameters_ShouldReturnTrainings() {
        // Arrange
        String traineeUsername = "testTrainee";
        String trainerUsername = "testTrainer";
        Date from = new Date();
        Date to = new Date();
        String trainingTypeName = "Strength Training";

        // Mock TypedQuery
        TypedQuery<Training> mockedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(mockedQuery);
        when(mockedQuery.getResultList()).thenReturn(Arrays.asList(training));

        // Act
        List<Training> trainings = trainingRepository.findAllTraineeTrainings(
                traineeUsername, trainerUsername, from, to, trainingTypeName
        );

        // Assert
        assertNotNull(trainings);
        assertEquals(1, trainings.size());
        verify(mockedQuery).setParameter("traineeUsername", traineeUsername);
        verify(mockedQuery).setParameter("trainerUsername", trainerUsername);
        verify(mockedQuery).setParameter("from", from);
        verify(mockedQuery).setParameter("to", to);
        verify(mockedQuery).setParameter("trainingTypeName", trainingTypeName);
    }

    @Test
    void findAllTrainerTrainings_WithAllParameters_ShouldReturnTrainings() {
        // Arrange
        String trainerUsername = "testTrainer";
        String traineeUsername = "testTrainee";
        Date from = new Date();
        Date to = new Date();

        // Mock TypedQuery
        TypedQuery<Training> mockedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(mockedQuery);
        when(mockedQuery.getResultList()).thenReturn(Arrays.asList(training));

        // Act
        List<Training> trainings = trainingRepository.findAllTrainerTrainings(
                trainerUsername, traineeUsername, from, to
        );

        // Assert
        assertNotNull(trainings);
        assertEquals(1, trainings.size());
        verify(mockedQuery).setParameter("trainerUsername", trainerUsername);
        verify(mockedQuery).setParameter("traineeUsername", traineeUsername);
        verify(mockedQuery).setParameter("from", from);
        verify(mockedQuery).setParameter("to", to);
    }
}