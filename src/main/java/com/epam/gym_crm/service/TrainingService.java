package com.epam.gym_crm.service;

import com.epam.gym_crm.dto.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.entity.Training;

import java.util.List;

public interface TrainingService {
    List<Training> getTraineeTrainings(GetTraineeTrainingsRequestDTO request);
    List<Training> getTrainerTrainings(GetTrainerTrainingsRequestDTO request);
    Training addTraining(AddTrainingRequestDTO request);
}
