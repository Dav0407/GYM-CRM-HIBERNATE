package com.epam.gym_crm.service;

import com.epam.gym_crm.dto.request.CreateTrainerProfileRequestDTO;
import com.epam.gym_crm.dto.request.UpdateTrainerProfileRequestDTO;
import com.epam.gym_crm.dto.response.TrainerResponseDTO;
import com.epam.gym_crm.entity.Trainer;

import java.util.List;

public interface TrainerService extends UserCreationService{
    TrainerResponseDTO createTrainerProfile(CreateTrainerProfileRequestDTO request);
    TrainerResponseDTO getTrainerById(Long id);
    TrainerResponseDTO getTrainerByUsername(String username);
    Trainer getTrainerEntityByUsername(String username);
    TrainerResponseDTO updateTrainerProfile(Long id, UpdateTrainerProfileRequestDTO request);
    void updateStatus(String username);
    List<TrainerResponseDTO> getNotAssignedTrainersByTraineeUsername(String traineeUsername);
    TrainerResponseDTO getTrainerResponseDTO(Trainer trainer);
}
