package com.epam.gym_crm.service_test;

import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.TraineeTrainer;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.repository.TraineeTrainerRepository;
import com.epam.gym_crm.service.TraineeService;
import com.epam.gym_crm.service.TrainerService;
import com.epam.gym_crm.service.service_impl.TraineeTrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeTrainerServiceImplTest {

    @Mock
    private TraineeTrainerRepository traineeTrainerRepository;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TraineeTrainerServiceImpl traineeTrainerService;

    private Trainee trainee;
    private Trainer trainer;
    private TraineeTrainer traineeTrainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);

        trainer = new Trainer();
        trainer.setId(2L);

        traineeTrainer = new TraineeTrainer();
        traineeTrainer.setId(3L);
        traineeTrainer.setTrainee(trainee);
        traineeTrainer.setTrainer(trainer);
    }

    @Test
    void createTraineeTrainer_ValidInput_CreatesRelationship() {
        String traineeUsername = "traineeUser";
        String trainerUsername = "trainerUser";

        when(traineeService.getTraineeByUsername(traineeUsername)).thenReturn(trainee);
        when(trainerService.getTrainerByUsername(trainerUsername)).thenReturn(trainer);
        when(traineeTrainerRepository.findByTraineeAndTrainer(trainee, trainer))
                .thenReturn(Optional.empty());
        when(traineeTrainerRepository.save(any(TraineeTrainer.class))).thenReturn(traineeTrainer);

        TraineeTrainer result = traineeTrainerService.createTraineeTrainer(traineeUsername, trainerUsername);

        assertThat(result).isEqualTo(traineeTrainer);
        verify(traineeTrainerRepository).save(any(TraineeTrainer.class));
    }

    @Test
    void createTraineeTrainer_RelationshipExists_ReturnsExisting() {
        String traineeUsername = "traineeUser";
        String trainerUsername = "trainerUser";

        when(traineeService.getTraineeByUsername(traineeUsername)).thenReturn(trainee);
        when(trainerService.getTrainerByUsername(trainerUsername)).thenReturn(trainer);
        when(traineeTrainerRepository.findByTraineeAndTrainer(trainee, trainer))
                .thenReturn(Optional.of(traineeTrainer));

        TraineeTrainer result = traineeTrainerService.createTraineeTrainer(traineeUsername, trainerUsername);

        assertThat(result).isEqualTo(traineeTrainer);
        verify(traineeTrainerRepository, never()).save(any(TraineeTrainer.class));
    }

    @Test
    void createTraineeTrainer_InvalidTraineeUsername_ThrowsException() {
        String traineeUsername = "";
        String trainerUsername = "trainerUser";

        assertThrows(IllegalArgumentException.class, () ->
                traineeTrainerService.createTraineeTrainer(traineeUsername, trainerUsername));
    }

    @Test
    void createTraineeTrainer_InvalidTrainerUsername_ThrowsException() {
        String traineeUsername = "traineeUser";
        String trainerUsername = "";

        assertThrows(IllegalArgumentException.class, () ->
                traineeTrainerService.createTraineeTrainer(traineeUsername, trainerUsername));
    }

    @Test
    void findByTraineeUsername_ReturnsRelationships() {
        String traineeUsername = "traineeUser";
        List<TraineeTrainer> expected = Collections.singletonList(traineeTrainer);

        when(traineeTrainerRepository.findAllByTraineeUsername(traineeUsername)).thenReturn(expected);

        List<TraineeTrainer> result = traineeTrainerService.findByTraineeUsername(traineeUsername);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void updateTraineeTrainers_ValidInput_UpdatesRelationships() {
        String traineeUsername = "traineeUser";
        List<String> trainerUsernames = List.of("trainerUser1", "trainerUser2");

        when(traineeService.getTraineeByUsername(traineeUsername)).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("trainerUser1")).thenReturn(trainer);
        when(trainerService.getTrainerByUsername("trainerUser2")).thenReturn(trainer);
        when(traineeTrainerRepository.findAllByTraineeUsername(traineeUsername))
                .thenReturn(Collections.singletonList(traineeTrainer));

        traineeTrainerService.updateTraineeTrainers(traineeUsername, trainerUsernames);

        verify(traineeTrainerRepository).deleteAll(any(List.class));
        verify(traineeTrainerRepository).saveAll(any(List.class));
    }

    @Test
    void updateTraineeTrainers_InvalidTraineeUsername_ThrowsException() {
        String traineeUsername = "";
        List<String> trainerUsernames = List.of("trainerUser1");

        assertThrows(IllegalArgumentException.class, () ->
                traineeTrainerService.updateTraineeTrainers(traineeUsername, trainerUsernames));
    }

    @Test
    void updateTraineeTrainers_NullTrainerUsernames_ThrowsException() {
        String traineeUsername = "traineeUser";

        assertThrows(IllegalArgumentException.class, () ->
                traineeTrainerService.updateTraineeTrainers(traineeUsername, null));
    }

    @Test
    void updateTraineeTrainers_TrainerNotFound_LogsWarning() {
        String traineeUsername = "traineeUser";
        List<String> trainerUsernames = List.of("invalidTrainer");

        when(traineeService.getTraineeByUsername(traineeUsername)).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("invalidTrainer")).thenReturn(null);
        when(traineeTrainerRepository.findAllByTraineeUsername(traineeUsername))
                .thenReturn(Collections.singletonList(traineeTrainer));

        traineeTrainerService.updateTraineeTrainers(traineeUsername, trainerUsernames);

        verify(traineeTrainerRepository).deleteAll(any(List.class));
        verify(traineeTrainerRepository).saveAll(any(List.class));
    }
}