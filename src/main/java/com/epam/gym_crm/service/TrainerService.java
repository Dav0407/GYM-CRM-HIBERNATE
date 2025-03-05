package com.epam.gym_crm.service;

import com.epam.gym_crm.dto.CreateTrainerProfileRequestDTO;
import com.epam.gym_crm.dto.UpdateTrainerProfileRequestDTO;
import com.epam.gym_crm.entity.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer createTrainerProfile(CreateTrainerProfileRequestDTO request);
    Trainer getTrainerById(Long id);
    Trainer getTrainerByUsername(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainer updateTrainerProfile(Long id, UpdateTrainerProfileRequestDTO request);
    void updateStatus(String username);
    List<Trainer> getNotAssignedTrainersByTraineeUsername(String traineeUsername);
}
