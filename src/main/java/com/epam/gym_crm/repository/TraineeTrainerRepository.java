package com.epam.gym_crm.repository;

import com.epam.gym_crm.entity.TraineeTrainer;

import java.util.List;
import java.util.Optional;

public interface TraineeTrainerRepository {
    Optional<TraineeTrainer> save(TraineeTrainer trainer);
    List<TraineeTrainer> findAllByTraineeUsername(String username);
}
