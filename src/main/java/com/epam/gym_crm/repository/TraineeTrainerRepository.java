package com.epam.gym_crm.repository;

import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.TraineeTrainer;
import com.epam.gym_crm.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TraineeTrainerRepository {

    TraineeTrainer save(TraineeTrainer trainer);

    List<TraineeTrainer> findAllByTraineeUsername(String username);

    Optional<TraineeTrainer> findByTraineeAndTrainer(Trainee trainee, Trainer trainer);

    void deleteAll(List<TraineeTrainer> existingRelations);

    void saveAll(List<TraineeTrainer> newRelations);
}
