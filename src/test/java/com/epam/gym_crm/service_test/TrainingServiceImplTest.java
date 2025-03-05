package com.epam.gym_crm.service_test;

import com.epam.gym_crm.dto.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.TraineeTrainer;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TrainingRepository;
import com.epam.gym_crm.service.TraineeTrainerService;
import com.epam.gym_crm.service.TrainingTypeService;
import com.epam.gym_crm.service_impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private TraineeTrainerService traineeTrainerService;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void getTraineeTrainings_ValidRequest_ReturnsTrainings() {
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("trainee");
        List<Training> expected = Collections.singletonList(new Training());

        when(trainingRepository.findAllTraineeTrainings(any(), any(), any(), any(), any()))
                .thenReturn(expected);

        List<Training> result = trainingService.getTraineeTrainings(request);

        assertFalse(result.isEmpty());
        verify(trainingRepository).findAllTraineeTrainings(
                "trainee", null, null, null, null
        );
    }

    @Test
    void getTraineeTrainings_InvalidUsername_ThrowsException() {
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraineeTrainings(request));
    }

    @Test
    void getTrainerTrainings_ValidRequest_ReturnsTrainings() {
        GetTrainerTrainingsRequestDTO request = new GetTrainerTrainingsRequestDTO();
        request.setTrainerUsername("trainer");
        List<Training> expected = Collections.singletonList(new Training());

        when(trainingRepository.findAllTrainerTrainings(any(), any(), any(), any()))
                .thenReturn(expected);

        List<Training> result = trainingService.getTrainerTrainings(request);

        assertFalse(result.isEmpty());
        verify(trainingRepository).findAllTrainerTrainings(
                "trainer", null, null, null
        );
    }

    @Test
    void addTraining_ValidRequest_ReturnsSavedTraining() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee");
        request.setTrainerUsername("trainer");
        request.setTrainingTypeName("Type");
        request.setTrainingDate(new Date());

        // Mock TrainingType
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Type");

        // Mock Trainee and Trainer
        Training expectedTraining = getTraining(trainingType, request);

        // Mock TraineeTrainer
        TraineeTrainer expectedRelation = new TraineeTrainer();

        // Mock dependencies
        when(trainingTypeService.findByValue("Type")).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(expectedTraining);
        when(traineeTrainerService.createTraineeTrainer("trainee", "trainer")).thenReturn(expectedRelation);

        // Act
        Training result = trainingService.addTraining(request);

        // Assert
        assertNotNull(result);
        assertEquals("trainee", result.getTrainee().getUser().getUsername());
        assertEquals("trainer", result.getTrainer().getUser().getUsername());
        assertEquals("Type", result.getTrainingType().getTrainingTypeName());
        verify(trainingRepository).save(any(Training.class));
        verify(traineeTrainerService).createTraineeTrainer("trainee", "trainer");
    }

    private static Training getTraining(TrainingType trainingType, AddTrainingRequestDTO request) {
        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setUsername("trainee");
        trainee.setUser(traineeUser);

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("trainer");
        trainer.setUser(trainerUser);

        // Mock Training
        Training expectedTraining = new Training();
        expectedTraining.setTrainee(trainee);
        expectedTraining.setTrainer(trainer);
        expectedTraining.setTrainingType(trainingType);
        expectedTraining.setTrainingDate(request.getTrainingDate());
        return expectedTraining;
    }

    @Test
    void addTraining_InvalidTrainingType_ThrowsException() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("trainee");
        request.setTrainerUsername("trainer");
        request.setTrainingTypeName("InvalidType");
        request.setTrainingDate(new Date());

        // Mock the behavior of trainingTypeService.findByValue()
        when(trainingTypeService.findByValue("InvalidType")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> trainingService.addTraining(request));
        assertEquals("Invalid training type: InvalidType", exception.getMessage());

        // Verify that trainingTypeService.findByValue() was called
        verify(trainingTypeService).findByValue("InvalidType");
    }

    @Test
    void addTraining_VerifyUsernameTrimming() {
        // Arrange
        AddTrainingRequestDTO request = new AddTrainingRequestDTO();
        request.setTraineeUsername("  trainee  ");
        request.setTrainerUsername("  trainer  ");
        request.setTrainingTypeName("Type");
        request.setTrainingDate(new Date());

        // Mock TrainingType
        TrainingType trainingType = new TrainingType();
        when(trainingTypeService.findByValue("Type")).thenReturn(Optional.of(trainingType));

        // Mock the save method and capture the Training object
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        when(trainingRepository.save(captor.capture())).thenReturn(new Training());

        // Mock TraineeTrainerService to return a valid TraineeTrainer object
        TraineeTrainer traineeTrainer = new TraineeTrainer();
        traineeTrainer.setId(1L); // Set a mock ID
        when(traineeTrainerService.createTraineeTrainer("trainee", "trainer")).thenReturn(traineeTrainer);

        // Act
        trainingService.addTraining(request);

        // Assert
        Training savedTraining = captor.getValue();
        assertNotNull(savedTraining.getTrainee());
        assertNotNull(savedTraining.getTrainer());
        assertEquals("trainee", savedTraining.getTrainee().getUser().getUsername());
        assertEquals("trainer", savedTraining.getTrainer().getUser().getUsername());

        // Verify interactions
        verify(trainingTypeService).findByValue("Type");
        verify(trainingRepository).save(any(Training.class));
        verify(traineeTrainerService).createTraineeTrainer("trainee", "trainer");
    }

    @Test
    void getTraineeTrainings_InvalidDateRange_ThrowsException() {
        GetTraineeTrainingsRequestDTO request = new GetTraineeTrainingsRequestDTO();
        request.setTraineeUsername("trainee");
        request.setFrom(new Date(System.currentTimeMillis() + 1000));
        request.setTo(new Date());

        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraineeTrainings(request));
    }
}