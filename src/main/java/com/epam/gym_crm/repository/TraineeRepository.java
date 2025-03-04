package com.epam.gym_crm.repository;

import com.epam.gym_crm.entity.Trainee;

import java.util.Optional;

public interface TraineeRepository {
    Optional<Trainee> save(Trainee trainee);
    Optional<Trainee> findById(Long id);
}
