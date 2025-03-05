package com.epam.gym_crm.service_test;

import com.epam.gym_crm.dto.CreateTrainerProfileRequestDTO;
import com.epam.gym_crm.dto.UpdateTrainerProfileRequestDTO;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TrainerRepository;
import com.epam.gym_crm.service.TrainingTypeService;
import com.epam.gym_crm.service.UserService;
import com.epam.gym_crm.service_impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTestTwo {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private CreateTrainerProfileRequestDTO createRequest;
    private UpdateTrainerProfileRequestDTO updateRequest;
    private User user;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        createRequest = new CreateTrainerProfileRequestDTO();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setTrainingType("Fitness");

        updateRequest = new UpdateTrainerProfileRequestDTO();
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setUsername("jane.smith");
        updateRequest.setTrainingTypeName("Yoga");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("password123");
        user.setIsActive(true);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);
    }

    @Test
    void testCreateTrainerProfile() {
        // Mock dependencies
        when(userService.generateUsername("John", "Doe")).thenReturn("john.doe");
        when(userService.generateRandomPassword()).thenReturn("randomPassword123");
        when(userService.saveUser(any(User.class))).thenReturn(user);
        when(trainingTypeService.findByValue("Fitness")).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        // Call the method
        Trainer createdTrainer = trainerService.createTrainerProfile(createRequest);

        // Assertions
        assertNotNull(createdTrainer);
        assertEquals("john.doe", createdTrainer.getUser().getUsername());
        assertEquals("Fitness", createdTrainer.getSpecialization().getTrainingTypeName());
        verify(userService, times(1)).generateUsername("John", "Doe");
        verify(userService, times(1)).generateRandomPassword();
        verify(userService, times(1)).saveUser(any(User.class));
        verify(trainingTypeService, times(1)).findByValue("Fitness");
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void testCreateTrainerProfileWithInvalidRequest() {
        createRequest.setFirstName("");
        createRequest.setLastName("");

        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.createTrainerProfile(createRequest);
        });

        verify(userService, never()).generateUsername(anyString(), anyString());
        verify(userService, never()).generateRandomPassword();
        verify(userService, never()).saveUser(any(User.class));
        verify(trainingTypeService, never()).findByValue(anyString());
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void testGetTrainerById() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer foundTrainer = trainerService.getTrainerById(1L);

        assertNotNull(foundTrainer);
        assertEquals(1L, foundTrainer.getId());
        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTrainerByIdNotFound() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            trainerService.getTrainerById(1L);
        });

        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTrainerByUsername() {
        when(userService.getUserByUsername("john.doe")).thenReturn(user);
        when(trainerRepository.findByUserId(1L)).thenReturn(Optional.of(trainer));

        Trainer foundTrainer = trainerService.getTrainerByUsername("john.doe");

        assertNotNull(foundTrainer);
        assertEquals(1L, foundTrainer.getId());
        verify(userService, times(1)).getUserByUsername("john.doe");
        verify(trainerRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetTrainerByUsernameNotFound() {
        when(userService.getUserByUsername("john.doe")).thenReturn(user);
        when(trainerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            trainerService.getTrainerByUsername("john.doe");
        });

        verify(userService, times(1)).getUserByUsername("john.doe");
        verify(trainerRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testChangePassword() {
        doNothing().when(userService).changePassword("john.doe", "oldPassword", "newPassword");

        trainerService.changePassword("john.doe", "oldPassword", "newPassword");

        verify(userService, times(1)).changePassword("john.doe", "oldPassword", "newPassword");
    }

    @Test
    void testUpdateTrainerProfile() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingTypeService.findByValue("Yoga")).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        Trainer updatedTrainer = trainerService.updateTrainerProfile(1L, updateRequest);

        assertNotNull(updatedTrainer);
        assertEquals("Jane", updatedTrainer.getUser().getFirstName());
        assertEquals("Fitness", updatedTrainer.getSpecialization().getTrainingTypeName());
        verify(trainerRepository, times(1)).findById(1L);
        verify(trainingTypeService, times(1)).findByValue("Yoga");
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainerProfileNotFound() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            trainerService.updateTrainerProfile(1L, updateRequest);
        });

        verify(trainerRepository, times(1)).findById(1L);
        verify(trainingTypeService, never()).findByValue(anyString());
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void testUpdateStatus() {
        doNothing().when(userService).updateStatus("john.doe");

        trainerService.updateStatus("john.doe");

        verify(userService, times(1)).updateStatus("john.doe");
    }

    @Test
    void testGetNotAssignedTrainersByTraineeUsername() {
        when(trainerRepository.findUnassignedTrainersByTraineeUsername("trainee1")).thenReturn(List.of(trainer));

        List<Trainer> unassignedTrainers = trainerService.getNotAssignedTrainersByTraineeUsername("trainee1");

        assertNotNull(unassignedTrainers);
        assertEquals(1, unassignedTrainers.size());
        verify(trainerRepository, times(1)).findUnassignedTrainersByTraineeUsername("trainee1");
    }

    @Test
    void testGetNotAssignedTrainersByTraineeUsernameWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.getNotAssignedTrainersByTraineeUsername("");
        });

        verify(trainerRepository, never()).findUnassignedTrainersByTraineeUsername(anyString());
    }
}