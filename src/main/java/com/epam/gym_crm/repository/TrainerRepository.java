package com.epam.gym_crm.repository;

import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);
    Optional<Trainer> findById(Long id);
    List<Trainer> findAll();
    Optional<Trainer> findByUserId(Long id);
    List<Trainer> findUnassignedTrainersByTraineeUsername(String traineeUsername);
}
