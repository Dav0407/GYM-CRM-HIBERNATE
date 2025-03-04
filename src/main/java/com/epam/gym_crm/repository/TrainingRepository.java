package com.epam.gym_crm.repository;

import com.epam.gym_crm.entity.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Optional<Training> save(Training training);
    Optional<Training> findById(Long id);
    List<Training> findAllTraineeTrainings(String traineeUsername, String trainerUsername, Date from, Date to, String trainingTypeName);
    List<Training> findAllTrainerTrainings(String trainerUsername, String traineeUsername, Date from, Date to);

}
