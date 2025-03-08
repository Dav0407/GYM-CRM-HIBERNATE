package com.epam.gym_crm.repository_impl;

import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.TraineeTrainer;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.repository.TraineeTrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeTrainerRepositoryImpl implements TraineeTrainerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public TraineeTrainer save(TraineeTrainer traineeTrainer) {
        try {
            if (traineeTrainer.getId() == null) {
                entityManager.persist(traineeTrainer);
            } else {
                traineeTrainer = entityManager.merge(traineeTrainer);
            }
            return traineeTrainer;
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

    @Override
    public Optional<TraineeTrainer> findByTraineeAndTrainer(Trainee trainee, Trainer trainer) {
        try {
            TraineeTrainer traineeTrainer = entityManager.createQuery(
                            "SELECT tt FROM TraineeTrainer tt WHERE tt.trainee = :trainee AND tt.trainer = :trainer",
                            TraineeTrainer.class)
                    .setParameter("trainee", trainee)
                    .setParameter("trainer", trainer)
                    .getSingleResult();
            return Optional.of(traineeTrainer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void deleteAll(List<TraineeTrainer> existingRelations) {
        if (existingRelations == null || existingRelations.isEmpty()) {
            return;
        }

        for (TraineeTrainer traineeTrainer : existingRelations) {
            entityManager.remove(entityManager.contains(traineeTrainer) ? traineeTrainer : entityManager.merge(traineeTrainer));
        }
    }

    @Override
    @Transactional
    public void saveAll(List<TraineeTrainer> newRelations) {
        if (newRelations == null || newRelations.isEmpty()) {
            return;
        }

        for (TraineeTrainer traineeTrainer : newRelations) {
            if (traineeTrainer.getId() == null) {
                entityManager.persist(traineeTrainer);
            } else {
                entityManager.merge(traineeTrainer);
            }
        }
    }


}

