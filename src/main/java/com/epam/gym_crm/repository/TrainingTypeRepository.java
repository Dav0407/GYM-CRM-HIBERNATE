package com.epam.gym_crm.repository;

import com.epam.gym_crm.entity.TrainingType;

import java.util.Optional;

public interface TrainingTypeRepository {
    TrainingType save(TrainingType trainingType);
    Optional<TrainingType> findById(Long id);
    Optional<TrainingType> findByValue(String trainingType);
}
