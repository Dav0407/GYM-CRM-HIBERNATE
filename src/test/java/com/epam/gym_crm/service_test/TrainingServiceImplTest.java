package com.epam.gym_crm.service_test;

import com.epam.gym_crm.dto.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.repository.TrainingRepository;
import com.epam.gym_crm.service.TraineeService;
import com.epam.gym_crm.service.TraineeTrainerService;
import com.epam.gym_crm.service.TrainerService;
import com.epam.gym_crm.service.TrainingTypeService;
import com.epam.gym_crm.service_impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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

    @Captor
    private ArgumentCaptor<Training> trainingCaptor;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private Date fromDate;
    private Date toDate;
    private List<Training> mockTrainings;

    @BeforeEach
    public void setUp() {
        // Create common test data
        trainee = new Trainee();
        trainee.setId(1L);

        trainer = new Trainer();
        trainer.setId(2L);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Cardio");

        // Create dates
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.JANUARY, 1);
        fromDate = calendar.getTime();

        calendar.set(2023, Calendar.DECEMBER, 31);
        toDate = calendar.getTime();

        // Create mock training list
        mockTrainings = new ArrayList<>();
        Training training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingDate(new Date());
        mockTrainings.add(training);
    }

    @Test
    public void testGetTraineeTrainings_NullTraineeUsername_ThrowsException() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTraineeTrainings(request));
        assertEquals("Trainee username cannot be empty.", exception.getMessage());
    }

    @Test
    public void testGetTraineeTrainings_EmptyTraineeUsername_ThrowsException() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("  ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTraineeTrainings(request));
        assertEquals("Trainee username cannot be empty.", exception.getMessage());
    }

    @Test
    public void testGetTraineeTrainings_InvalidDateRange_ThrowsException() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("trainee1");
        request.setFrom(toDate);  // Later date
        request.setTo(fromDate);  // Earlier date

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTraineeTrainings(request));
        assertEquals("Invalid date range: 'from' date cannot be after 'to' date.", exception.getMessage());
    }

    @Test
    public void testGetTraineeTrainings_InvalidTrainer_ThrowsException() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("trainee1");
        request.setTrainerUsername("nonexistent");

        when(trainerService.getTrainerByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTraineeTrainings(request));
        assertEquals("Trainer not found with username: nonexistent", exception.getMessage());
    }

    @Test
    public void testGetTrainerTrainings_ValidRequest_ReturnsTrainings() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("trainer1");
        request.setFrom(fromDate);
        request.setTo(toDate);

        when(trainerService.getTrainerByUsername("trainer1")).thenReturn(trainer);
        when(trainingRepository.findAllTrainerTrainings(
                eq("trainer1"), eq(null), eq(fromDate), eq(toDate)))
                .thenReturn(mockTrainings);

        // Act
        List<Training> result = trainingService.getTrainerTrainings(request);

        // Assert
        assertEquals(mockTrainings.size(), result.size());
        assertEquals(mockTrainings, result);
    }

    @Test
    public void testGetTrainerTrainings_WithTraineeFilter_ReturnsFilteredTrainings() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("trainer1");
        request.setTraineeUsername("trainee1");
        request.setFrom(fromDate);
        request.setTo(toDate);

        when(trainerService.getTrainerByUsername("trainer1")).thenReturn(trainer);
        when(traineeService.getTraineeByUsername("trainee1")).thenReturn(trainee);
        when(trainingRepository.findAllTrainerTrainings(
                eq("trainer1"), eq("trainee1"), eq(fromDate), eq(toDate)))
                .thenReturn(mockTrainings);

        // Act
        List<Training> result = trainingService.getTrainerTrainings(request);

        // Assert
        assertEquals(mockTrainings.size(), result.size());
        assertEquals(mockTrainings, result);
    }

    @Test
    public void testGetTrainerTrainings_NullTrainerUsername_ThrowsException() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTrainerTrainings(request));
        assertEquals("Trainer username cannot be empty.", exception.getMessage());
    }

    @Test
    public void testGetTrainerTrainings_EmptyTrainerUsername_ThrowsException() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("  ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTrainerTrainings(request));
        assertEquals("Trainer username cannot be empty.", exception.getMessage());
    }

    @Test
    public void testGetTrainerTrainings_NonexistentTrainer_ThrowsException() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("nonexistent");

        when(trainerService.getTrainerByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTrainerTrainings(request));
        assertEquals("Trainer not found with username: nonexistent", exception.getMessage());
    }

    @Test
    public void testGetTrainerTrainings_InvalidTrainee_ThrowsException() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("trainer1");
        request.setTraineeUsername("nonexistent");

        when(trainerService.getTrainerByUsername("trainer1")).thenReturn(trainer);
        when(traineeService.getTraineeByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTrainerTrainings(request));
        assertEquals("Trainee not found with username: nonexistent", exception.getMessage());
    }

    @Test
    public void testAddTraining_ValidRequest_ReturnsTraining() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee1");
        request.setTrainerUsername("trainer1");
        request.setTrainingTypeName("Cardio");
        request.setTrainingDate(new Date());

        when(traineeService.getTraineeByUsername("trainee1")).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("trainer1")).thenReturn(trainer);
        when(trainingTypeService.findByValue("Cardio")).thenReturn(Optional.of(trainingType));

        Training savedTraining = new Training();
        savedTraining.setId(1L);
        when(trainingRepository.save(any(Training.class))).thenReturn(savedTraining);

        // Act
        Training result = trainingService.addTraining(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(trainingRepository).save(trainingCaptor.capture());
        Training capturedTraining = trainingCaptor.getValue();
        assertEquals(trainee, capturedTraining.getTrainee());
        assertEquals(trainer, capturedTraining.getTrainer());
        assertEquals(trainingType, capturedTraining.getTrainingType());
        assertEquals(request.getTrainingDate(), capturedTraining.getTrainingDate());

        verify(traineeTrainerService).createTraineeTrainer("trainee1", "trainer1");
    }

    @Test
    public void testAddTraining_NullTraineeUsername_ThrowsException() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername(null);
        request.setTrainerUsername("trainer1");
        request.setTrainingTypeName("Cardio");
        request.setTrainingDate(new Date());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.addTraining(request));
        assertEquals("Trainee username cannot be empty.", exception.getMessage());

        verify(trainingRepository, never()).save(any(Training.class));
        verify(traineeTrainerService, never()).createTraineeTrainer(anyString(), anyString());
    }

    @Test
    public void testAddTraining_NullTrainerUsername_ThrowsException() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee1");
        request.setTrainerUsername(null);
        request.setTrainingTypeName("Cardio");
        request.setTrainingDate(new Date());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.addTraining(request));
        assertEquals("Trainer username cannot be empty.", exception.getMessage());

        verify(trainingRepository, never()).save(any(Training.class));
        verify(traineeTrainerService, never()).createTraineeTrainer(anyString(), anyString());
    }

    @Test
    public void testAddTraining_NullTrainingDate_ThrowsException() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee1");
        request.setTrainerUsername("trainer1");
        request.setTrainingTypeName("Cardio");
        request.setTrainingDate(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.addTraining(request));
        assertEquals("Training date cannot be null.", exception.getMessage());

        verify(trainingRepository, never()).save(any(Training.class));
        verify(traineeTrainerService, never()).createTraineeTrainer(anyString(), anyString());
    }

    @Test
    public void testAddTraining_TraineeNotFound_ThrowsException() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("nonexistent");
        request.setTrainerUsername("trainer1");
        request.setTrainingTypeName("Cardio");
        request.setTrainingDate(new Date());

        when(traineeService.getTraineeByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.addTraining(request));
        assertEquals("No trainee found with username: nonexistent", exception.getMessage());

        verify(trainingRepository, never()).save(any(Training.class));
        verify(traineeTrainerService, never()).createTraineeTrainer(anyString(), anyString());
    }

    @Test
    public void testAddTraining_TrainerNotFound_ThrowsException() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee1");
        request.setTrainerUsername("nonexistent");
        request.setTrainingTypeName("Cardio");
        request.setTrainingDate(new Date());

        when(traineeService.getTraineeByUsername("trainee1")).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("nonexistent")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.addTraining(request));
        assertEquals("No trainer found with username: nonexistent", exception.getMessage());

        verify(trainingRepository, never()).save(any(Training.class));
        verify(traineeTrainerService, never()).createTraineeTrainer(anyString(), anyString());
    }

    @Test
    public void testAddTraining_InvalidTrainingType_ThrowsException() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee1");
        request.setTrainerUsername("trainer1");
        request.setTrainingTypeName("InvalidType");
        request.setTrainingDate(new Date());

        when(traineeService.getTraineeByUsername("trainee1")).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("trainer1")).thenReturn(trainer);
        when(trainingTypeService.findByValue("InvalidType")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trainingService.addTraining(request));
        assertEquals("Invalid training type: InvalidType", exception.getMessage());

        verify(trainingRepository, never()).save(any(Training.class));
        verify(traineeTrainerService, never()).createTraineeTrainer(anyString(), anyString());
    }
}