package com.epam.gym_crm.service;

import com.epam.gym_crm.entity.TrainingType;

import java.util.Optional;

public interface TrainingTypeService {
    Optional<TrainingType> findByValue(String value);
}
