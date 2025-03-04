package com.epam.gym_crm.service;

import com.epam.gym_crm.dto.CreateTraineeProfileRequestDTO;
import com.epam.gym_crm.entity.Trainee;

public interface TraineeService {
    Trainee createTraineeProfile(CreateTraineeProfileRequestDTO request);
    Trainee getTraineeById(Long id);
}
