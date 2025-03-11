package com.epam.gym_crm.service_test;

import com.epam.gym_crm.dto.request.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.request.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.request.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.dto.response.TraineeResponseDTO;
import com.epam.gym_crm.dto.response.TrainerResponseDTO;
import com.epam.gym_crm.dto.response.TrainingResponseDTO;
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
import com.epam.gym_crm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

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

    private Trainee traineeEntity;
    private Trainer trainerEntity;
    private TraineeResponseDTO traineeResponseDTO;
    private TrainerResponseDTO trainerResponseDTO;
    private Training training;
    private TrainingType trainingType;
    private Date trainingDate;

    @BeforeEach
    void setUp() {
        trainingDate = new Date();

        // Create User entities
        User traineeUser = new User();
        traineeUser.setUsername("trainee.username");

        User trainerUser = new User();
        trainerUser.setUsername("trainer.username");

        // Create entities
        traineeEntity = new Trainee();
        traineeEntity.setId(1L);
        traineeEntity.setUser(traineeUser);

        trainerEntity = new Trainer();
        trainerEntity.setId(1L);
        trainerEntity.setUser(trainerUser);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Cardio");

        training = new Training();
        training.setId(1L);
        training.setTrainee(traineeEntity);
        training.setTrainer(trainerEntity);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(60);
        training.setTrainingName("Morning Run");

        // Create response DTOs
        traineeResponseDTO = new TraineeResponseDTO();
        traineeResponseDTO.setId(traineeEntity.getId());
        traineeResponseDTO.setUsername(traineeEntity.getUser().getUsername());

        trainerResponseDTO = new TrainerResponseDTO();
        trainerResponseDTO.setId(trainerEntity.getId());
        trainerResponseDTO.setUsername(trainerEntity.getUser().getUsername());
    }

    @Test
    void testGetTraineeTrainings_Success() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setFrom(new Date(System.currentTimeMillis() - 1000)); // Past date
        request.setTo(new Date());

        when(trainingRepository.findAllTraineeTrainings(
                "trainee.username", null, request.getFrom(), request.getTo(), null))
                .thenReturn(Collections.singletonList(training));

        when(traineeService.getTraineeResponseDTO(traineeEntity)).thenReturn(traineeResponseDTO);
        when(trainerService.getTrainerResponseDTO(trainerEntity)).thenReturn(trainerResponseDTO);

        // Act
        List<TrainingResponseDTO> result = trainingService.getTraineeTrainings(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Morning Run", result.get(0).getTrainingName());
        assertEquals("Cardio", result.get(0).getTrainingType());

        verify(trainingRepository, times(1)).findAllTraineeTrainings(
                "trainee.username", null, request.getFrom(), request.getTo(), null);
    }

    @Test
    void testGetTraineeTrainings_InvalidTraineeUsername() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraineeTrainings(request));

        verify(trainingRepository, times(0)).findAllTraineeTrainings(
                anyString(), anyString(), any(), any(), anyString());
    }

    @Test
    void testGetTraineeTrainings_InvalidDateRange() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setFrom(new Date(System.currentTimeMillis() + 1000)); // Future date
        request.setTo(new Date());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraineeTrainings(request));

        verify(trainingRepository, times(0)).findAllTraineeTrainings(
                anyString(), anyString(), any(), any(), anyString());
    }

    @Test
    void testGetTraineeTrainings_WithTrainerUsername() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setTrainerUsername("trainer.username");

        when(trainerService.getTrainerByUsername("trainer.username")).thenReturn(trainerResponseDTO);
        when(trainingRepository.findAllTraineeTrainings(
                "trainee.username", "trainer.username", null, null, null))
                .thenReturn(Collections.singletonList(training));

        when(traineeService.getTraineeResponseDTO(traineeEntity)).thenReturn(traineeResponseDTO);
        when(trainerService.getTrainerResponseDTO(trainerEntity)).thenReturn(trainerResponseDTO);

        // Act
        List<TrainingResponseDTO> result = trainingService.getTraineeTrainings(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainerService, times(1)).getTrainerByUsername("trainer.username");
    }

    @Test
    void testGetTraineeTrainings_TrainerNotFound() {
        // Arrange
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setTrainerUsername("non.existent.trainer");

        when(trainerService.getTrainerByUsername("non.existent.trainer")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraineeTrainings(request));

        verify(trainerService, times(1)).getTrainerByUsername("non.existent.trainer");
        verify(trainingRepository, times(0)).findAllTraineeTrainings(
                anyString(), anyString(), any(), any(), anyString());
    }

    @Test
    void testGetTrainerTrainings_Success() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("trainer.username");
        request.setFrom(new Date(System.currentTimeMillis() - 1000)); // Past date
        request.setTo(new Date());

        // Mock the trainerService to return a valid Trainer object
        when(trainerService.getTrainerByUsername("trainer.username")).thenReturn(trainerResponseDTO);

        // Mock the trainingRepository to return a list of trainings
        when(trainingRepository.findAllTrainerTrainings(
                "trainer.username", null, request.getFrom(), request.getTo()))
                .thenReturn(Collections.singletonList(training));

        when(traineeService.getTraineeResponseDTO(traineeEntity)).thenReturn(traineeResponseDTO);
        when(trainerService.getTrainerResponseDTO(trainerEntity)).thenReturn(trainerResponseDTO);

        // Act
        List<TrainingResponseDTO> result = trainingService.getTrainerTrainings(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Morning Run", result.get(0).getTrainingName());

        verify(trainerService, times(1)).getTrainerByUsername("trainer.username");
        verify(trainingRepository, times(1)).findAllTrainerTrainings(
                "trainer.username", null, request.getFrom(), request.getTo());
    }

    @Test
    void testGetTrainerTrainings_InvalidTrainerUsername() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainerTrainings(request));

        verify(trainingRepository, times(0)).findAllTrainerTrainings(
                anyString(), anyString(), any(), any());
    }

    @Test
    void testGetTrainerTrainings_InvalidDateRange() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("trainer.username");
        request.setFrom(new Date(System.currentTimeMillis() + 1000)); // Future date
        request.setTo(new Date());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainerTrainings(request));

        verify(trainingRepository, times(0)).findAllTrainerTrainings(
                anyString(), anyString(), any(), any());
    }

    @Test
    void testGetTrainerTrainings_TrainerNotFound() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("non.existent.trainer");

        when(trainerService.getTrainerByUsername("non.existent.trainer")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainerTrainings(request));

        verify(trainerService, times(1)).getTrainerByUsername("non.existent.trainer");
        verify(trainingRepository, times(0)).findAllTrainerTrainings(
                anyString(), anyString(), any(), any());
    }

    @Test
    void testGetTrainerTrainings_WithTraineeUsername() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("trainer.username");
        request.setTraineeUsername("trainee.username");

        when(trainerService.getTrainerByUsername("trainer.username")).thenReturn(trainerResponseDTO);
        when(traineeService.getTraineeByUsername("trainee.username")).thenReturn(traineeResponseDTO);

        when(trainingRepository.findAllTrainerTrainings(
                "trainer.username", "trainee.username", null, null))
                .thenReturn(Collections.singletonList(training));

        when(traineeService.getTraineeResponseDTO(traineeEntity)).thenReturn(traineeResponseDTO);
        when(trainerService.getTrainerResponseDTO(trainerEntity)).thenReturn(trainerResponseDTO);

        // Act
        List<TrainingResponseDTO> result = trainingService.getTrainerTrainings(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(traineeService, times(1)).getTraineeByUsername("trainee.username");
    }

    @Test
    void testGetTrainerTrainings_TraineeNotFound() {
        // Arrange
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("trainer.username");
        request.setTraineeUsername("non.existent.trainee");

        when(trainerService.getTrainerByUsername("trainer.username")).thenReturn(trainerResponseDTO);
        when(traineeService.getTraineeByUsername("non.existent.trainee")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTrainerTrainings(request));

        verify(trainerService, times(1)).getTrainerByUsername("trainer.username");
        verify(traineeService, times(1)).getTraineeByUsername("non.existent.trainee");
        verify(trainingRepository, times(0)).findAllTrainerTrainings(
                anyString(), anyString(), any(), any());
    }

    @Test
    void testAddTraining_Success() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setTrainerUsername("trainer.username");
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(60);
        request.setTrainingName("Morning Run");
        request.setTrainingTypeName("Cardio");

        // Mock the necessary dependencies
        when(traineeService.getTraineeEntityByUsername("trainee.username")).thenReturn(traineeEntity);
        when(trainerService.getTrainerEntityByUsername("trainer.username")).thenReturn(trainerEntity);
        when(trainingTypeService.findByValue("Cardio")).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);
        when(traineeService.getTraineeResponseDTO(traineeEntity)).thenReturn(traineeResponseDTO);
        when(trainerService.getTrainerResponseDTO(trainerEntity)).thenReturn(trainerResponseDTO);

        // Act
        TrainingResponseDTO result = trainingService.addTraining(request);

        // Assert
        assertNotNull(result);
        assertEquals("Morning Run", result.getTrainingName());
        assertEquals("Cardio", result.getTrainingType());

        // Verify interactions
        verify(traineeService, times(1)).getTraineeEntityByUsername("trainee.username");
        verify(trainerService, times(1)).getTrainerEntityByUsername("trainer.username");
        verify(trainingTypeService, times(1)).findByValue("Cardio");
        verify(trainingRepository, times(1)).save(any(Training.class));
        verify(traineeTrainerService, times(1)).createTraineeTrainer("trainee.username", "trainer.username");
    }

    @Test
    void testAddTraining_InvalidTraineeUsername() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("");
        request.setTrainerUsername("trainer.username");
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(60);
        request.setTrainingName("Morning Run");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));

        verify(traineeService, times(0)).getTraineeEntityByUsername(anyString());
        verify(trainerService, times(0)).getTrainerEntityByUsername(anyString());
        verify(trainingTypeService, times(0)).findByValue(anyString());
        verify(trainingRepository, times(0)).save(any(Training.class));
        verify(traineeTrainerService, times(0)).createTraineeTrainer(anyString(), anyString());
    }

    @Test
    void testAddTraining_InvalidTrainerUsername() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setTrainerUsername("");
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(60);
        request.setTrainingName("Morning Run");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));

        verify(traineeService, times(0)).getTraineeEntityByUsername(anyString());
        verify(trainerService, times(0)).getTrainerEntityByUsername(anyString());
        verify(trainingTypeService, times(0)).findByValue(anyString());
        verify(trainingRepository, times(0)).save(any(Training.class));
        verify(traineeTrainerService, times(0)).createTraineeTrainer(anyString(), anyString());
    }

    @Test
    void testAddTraining_TrainingDateNull() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setTrainerUsername("trainer.username");
        request.setTrainingDate(null);
        request.setTrainingDuration(60);
        request.setTrainingName("Morning Run");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));

        verify(traineeService, times(0)).getTraineeEntityByUsername(anyString());
        verify(trainerService, times(0)).getTrainerEntityByUsername(anyString());
    }

    @Test
    void testAddTraining_TrainingDurationNull() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setTrainerUsername("trainer.username");
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(null);
        request.setTrainingName("Morning Run");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));

        verify(traineeService, times(0)).getTraineeEntityByUsername(anyString());
        verify(trainerService, times(0)).getTrainerEntityByUsername(anyString());
    }

    @Test
    void testAddTraining_TraineeNotFound() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("non.existent.trainee");
        request.setTrainerUsername("trainer.username");
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(60);
        request.setTrainingName("Morning Run");
        request.setTrainingTypeName("Cardio");

        when(traineeService.getTraineeEntityByUsername("non.existent.trainee")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));

        verify(traineeService, times(1)).getTraineeEntityByUsername("non.existent.trainee");
        verify(trainerService, times(0)).getTrainerEntityByUsername(anyString());
        verify(trainingTypeService, times(0)).findByValue(anyString());
    }

    @Test
    void testAddTraining_TrainerNotFound() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setTrainerUsername("non.existent.trainer");
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(60);
        request.setTrainingName("Morning Run");
        request.setTrainingTypeName("Cardio");

        when(traineeService.getTraineeEntityByUsername("trainee.username")).thenReturn(traineeEntity);
        when(trainerService.getTrainerEntityByUsername("non.existent.trainer")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));

        verify(traineeService, times(1)).getTraineeEntityByUsername("trainee.username");
        verify(trainerService, times(1)).getTrainerEntityByUsername("non.existent.trainer");
        verify(trainingTypeService, times(0)).findByValue(anyString());
    }

    @Test
    void testAddTraining_InvalidTrainingType() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee.username");
        request.setTrainerUsername("trainer.username");
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(60);
        request.setTrainingName("Morning Run");
        request.setTrainingTypeName("InvalidType");

        when(traineeService.getTraineeEntityByUsername("trainee.username")).thenReturn(traineeEntity);
        when(trainerService.getTrainerEntityByUsername("trainer.username")).thenReturn(trainerEntity);
        when(trainingTypeService.findByValue("InvalidType")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));

        verify(traineeService, times(1)).getTraineeEntityByUsername("trainee.username");
        verify(trainerService, times(1)).getTrainerEntityByUsername("trainer.username");
        verify(trainingTypeService, times(1)).findByValue("InvalidType");
        verify(trainingRepository, times(0)).save(any(Training.class));
        verify(traineeTrainerService, times(0)).createTraineeTrainer(anyString(), anyString());
    }
}