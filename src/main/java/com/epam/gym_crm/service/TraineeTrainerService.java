package com.epam.gym_crm.service;

import com.epam.gym_crm.entity.TraineeTrainer;

import java.util.List;

public interface TraineeTrainerService {
    TraineeTrainer createTraineeTrainer(String traineeUsername, String trainerUsername);
    List<TraineeTrainer> findByTraineeUsername(String traineeUsername);
    void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames);
}
