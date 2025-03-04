package com.epam.gym_crm.service;

import com.epam.gym_crm.dto.CreateTrainerProfileRequestDTO;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.User;

public interface TrainerService {
    Trainer createTrainerProfile(CreateTrainerProfileRequestDTO request);
    Trainer getTrainerById(Long id);
}
