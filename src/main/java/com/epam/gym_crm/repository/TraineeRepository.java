package com.epam.gym_crm.repository;

import com.epam.gym_crm.entity.Trainee;

import java.util.Optional;

public interface TraineeRepository {
    Trainee save(Trainee trainee);
    Optional<Trainee> findById(Long id);
    Optional<Trainee> findByUserId(Long userId);
}
