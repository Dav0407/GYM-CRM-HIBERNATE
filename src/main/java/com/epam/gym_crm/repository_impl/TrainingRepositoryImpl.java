package com.epam.gym_crm.repository_impl;

import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.repository.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Optional<Training> save(Training training) {
        try {
            if (training.getId() == null) {
                entityManager.persist(training);
            } else {
                training = entityManager.merge(training);
            }
            return Optional.of(training);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save Training: " + training, e);
        }
    }

    @Override
    public Optional<Training> findById(Long id) {
        Training training = entityManager.find(Training.class, id);
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> findAllTraineeTrainings(String traineeUsername, String trainerUsername, Date from, Date to, String trainingTypeName) {
        String queryStr = "SELECT t FROM Training t WHERE t.trainee.user.username = :traineeUsername";

        if (trainerUsername != null) queryStr += " AND t.trainer.username = :trainerUsername";
        if (from != null) queryStr += " AND t.date >= :from";
        if (to != null) queryStr += " AND t.date <= :to";
        if (trainingTypeName != null) queryStr += " AND t.trainingType.name = :trainingTypeName";

        TypedQuery<Training> query = entityManager.createQuery(queryStr, Training.class);
        query.setParameter("traineeUsername", traineeUsername);
        if (trainerUsername != null) query.setParameter("trainerUsername", trainerUsername);
        if (from != null) query.setParameter("from", from);
        if (to != null) query.setParameter("to", to);
        if (trainingTypeName != null) query.setParameter("trainingTypeName", trainingTypeName);

        return query.getResultList();
    }

    @Override
    public List<Training> findAllTrainerTrainings(String trainerUsername, String traineeUsername, Date from, Date to) {
        String queryStr = "SELECT t FROM Training t WHERE t.trainer.user.username = :trainerUsername";

        if (traineeUsername != null) queryStr += " AND t.trainee.username = :traineeUsername";
        if (from != null) queryStr += " AND t.date >= :from";
        if (to != null) queryStr += " AND t.date <= :to";

        TypedQuery<Training> query = entityManager.createQuery(queryStr, Training.class);
        query.setParameter("trainerUsername", trainerUsername);
        if (traineeUsername != null) query.setParameter("traineeUsername", traineeUsername);
        if (from != null) query.setParameter("from", from);
        if (to != null) query.setParameter("to", to);

        return query.getResultList();
    }
}

