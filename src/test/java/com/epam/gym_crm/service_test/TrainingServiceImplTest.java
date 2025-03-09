package com.epam.gym_crm.service_test;

import com.epam.gym_crm.dto.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TrainingRepository;
import com.epam.gym_crm.service.TraineeService;
import com.epam.gym_crm.service.TraineeTrainerService;
import com.epam.gym_crm.service.TrainerService;
import com.epam.gym_crm.service.TrainingTypeService;
import com.epam.gym_crm.service.service_impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private TraineeTrainerService traineeTrainerService;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private Training training;
    private Date fromDate;
    private Date toDate;

    @BeforeEach
    public void setup() {
        // Create test data
        User traineeUser = new User();
        traineeUser.setId(1L);
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        traineeUser.setUsername("john.doe");

        User trainerUser = new User();
        trainerUser.setId(2L);
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");
        trainerUser.setUsername("jane.smith");

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(traineeUser);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Cardio");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(trainerUser);
        trainer.setSpecialization(trainingType);

        training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName("Morning Cardio");
        training.setTrainingDuration(60);

        // Setup dates
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.JANUARY, 1);
        fromDate = calendar.getTime();

        calendar.set(2023, Calendar.DECEMBER, 31);
        toDate = calendar.getTime();

        training.setTrainingDate(new Date());
    }

    @Test
    public void testGetTraineeTrainings_Success() {
        // Arrange
        String traineeUsername = "john.doe";
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername(traineeUsername);
        request.setFrom(fromDate);
        request.setTo(toDate);

        List<Training> expectedTrainings = new ArrayList<>();
        expectedTrainings.add(training);

        when(trainingRepository.findAllTraineeTrainings(
                eq(traineeUsername),
                eq(null),
                eq(fromDate),
                eq(toDate),
                eq(null)))
                .thenReturn(expectedTrainings);

        // Act
        List<Training> result = trainingService.getTraineeTrainings(request);

        // Assert
        assertEquals(expectedTrainings.size(), result.size());
        assertEquals(expectedTrainings.get(0).getId(), result.get(0).getId());
    }

    @Test
    public void testGetTraineeTrainings_EmptyUsername() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraineeTrainings(request));
    }

    @Test
    public void testGetTraineeTrainings_InvalidDateRange() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("john.doe");
        request.setFrom(toDate);  // From date is after To date
        request.setTo(fromDate);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraineeTrainings(request));
    }

    @Test
    public void testGetTraineeTrainings_InvalidTrainer() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("nonexistent.trainer");

        when(trainerService.getTrainerByUsername("nonexistent.trainer")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraineeTrainings(request));
    }

    @Test
    public void testGetTrainerTrainings_Success() {
        // Arrange
        String trainerUsername = "jane.smith";
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername(trainerUsername);
        request.setFrom(fromDate);
        request.setTo(toDate);

        List<Training> expectedTrainings = new ArrayList<>();
        expectedTrainings.add(training);

        when(trainerService.getTrainerByUsername(trainerUsername)).thenReturn(trainer);
        when(trainingRepository.findAllTrainerTrainings(eq(trainerUsername), any(), eq(fromDate), eq(toDate)))
                .thenReturn(expectedTrainings);

        // Act
        List<Training> result = trainingService.getTrainerTrainings(request);

        // Assert
        assertEquals(expectedTrainings.size(), result.size());
        assertEquals(expectedTrainings.get(0).getId(), result.get(0).getId());
    }

    @Test
    public void testGetTrainerTrainings_EmptyUsername() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainerTrainings(request));
    }

    @Test
    public void testGetTrainerTrainings_TrainerNotFound() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("nonexistent.trainer");

        when(trainerService.getTrainerByUsername("nonexistent.trainer")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainerTrainings(request));
    }

    @Test
    public void testGetTrainerTrainings_InvalidTrainee() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("jane.smith");
        request.setTraineeUsername("nonexistent.trainee");

        when(trainerService.getTrainerByUsername("jane.smith")).thenReturn(trainer);
        when(traineeService.getTraineeByUsername("nonexistent.trainee")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainerTrainings(request));
    }

    @Test
    public void testAddTraining_Success() {
        // Arrange
        String traineeUsername = "john.doe";
        String trainerUsername = "jane.smith";
        String trainingTypeName = "Cardio";
        Date trainingDate = new Date();
        int trainingDuration = 60;
        String trainingName = "Morning Cardio";

        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername(traineeUsername);
        request.setTrainerUsername(trainerUsername);
        request.setTrainingTypeName(trainingTypeName);
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(trainingDuration);
        request.setTrainingName(trainingName);

        when(traineeService.getTraineeByUsername(traineeUsername)).thenReturn(trainee);
        when(trainerService.getTrainerByUsername(trainerUsername)).thenReturn(trainer);
        when(trainingTypeService.findByValue(trainingTypeName)).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        // Act
        Training result = trainingService.addTraining(request);

        // Assert
        assertNotNull(result);
        assertEquals(training.getId(), result.getId());
        verify(traineeTrainerService).createTraineeTrainer(traineeUsername, trainerUsername);
    }

    @Test
    public void testAddTraining_EmptyTraineeUsername() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));
    }

    @Test
    public void testAddTraining_EmptyTrainerUsername() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));
    }

    @Test
    public void testAddTraining_NullTrainingDate() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("jane.smith");
        request.setTrainingDate(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));
    }

    @Test
    public void testAddTraining_NullTrainingDuration() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("jane.smith");
        request.setTrainingDate(new Date());
        request.setTrainingDuration(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));
    }

    @Test
    public void testAddTraining_TraineeNotFound() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("nonexistent.trainee");
        request.setTrainerUsername("jane.smith");
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);
        request.setTrainingName("Test Training");

        when(traineeService.getTraineeByUsername("nonexistent.trainee")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));
    }

    @Test
    public void testAddTraining_TrainerNotFound() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("nonexistent.trainer");
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);
        request.setTrainingName("Test Training");

        when(traineeService.getTraineeByUsername("john.doe")).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("nonexistent.trainer")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));
    }

    @Test
    public void testAddTraining_InvalidTrainingType() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("jane.smith");
        request.setTrainingDate(new Date());
        request.setTrainingDuration(60);
        request.setTrainingName("Test Training");
        request.setTrainingTypeName("Invalid");

        when(traineeService.getTraineeByUsername("john.doe")).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("jane.smith")).thenReturn(trainer);
        when(trainingTypeService.findByValue("Invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));
    }
}