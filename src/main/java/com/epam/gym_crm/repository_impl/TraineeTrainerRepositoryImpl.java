package com.epam.gym_crm.repository_impl;

import com.epam.gym_crm.entity.TraineeTrainer;
import com.epam.gym_crm.repository.TraineeTrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeTrainerRepositoryImpl implements TraineeTrainerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Optional<TraineeTrainer> save(TraineeTrainer traineeTrainer) {
        try {
            if (traineeTrainer.getId() == null) {
                entityManager.persist(traineeTrainer);
            } else {
                traineeTrainer = entityManager.merge(traineeTrainer);
            }
            return Optional.of(traineeTrainer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save TraineeTrainer: " + traineeTrainer, e);
        }
    }

    @Override
    public List<TraineeTrainer> findAllByTraineeUsername(String username) {
        return entityManager.createQuery(
                        "SELECT tt FROM TraineeTrainer tt WHERE tt.trainee.user.username = :username",
                        TraineeTrainer.class)
                .setParameter("username", username)
                .getResultList();
    }
}

