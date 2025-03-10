package com.epam.gym_crm.service;

import com.epam.gym_crm.dto.request.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.request.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.request.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.dto.response.TrainingResponseDTO;

import java.util.List;

public interface TrainingService {
    List<TrainingResponseDTO> getTraineeTrainings(GetTraineeTrainingsRequestDTO request);
    List<TrainingResponseDTO> getTrainerTrainings(GetTrainerTrainingsRequestDTO request);
    TrainingResponseDTO addTraining(AddTrainingRequestDTO request);
}
