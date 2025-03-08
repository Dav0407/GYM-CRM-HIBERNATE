package com.epam.gym_crm.repository_impl;

import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.repository.TrainingTypeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public TrainingType save(TrainingType trainingType) {
        try {
            if (trainingType.getId() == null) {
                entityManager.persist(trainingType);
            } else {
                trainingType = entityManager.merge(trainingType);
            }
            return trainingType;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save TrainingType: " + trainingType, e);
        }
    }

    @Override
    public Optional<TrainingType> findById(Long id) {
        TrainingType trainingType = entityManager.find(TrainingType.class, id);
        return Optional.ofNullable(trainingType);
    }

    @Override
    public Optional<TrainingType> findByValue(String trainingType) {
        try {
            TrainingType result = entityManager.createQuery(
                            "SELECT t FROM TrainingType t WHERE LOWER(t.trainingTypeName) = LOWER(:trainingType)", TrainingType.class)
                    .setParameter("trainingType", trainingType.toLowerCase())
                    .getSingleResult();

            return Optional.ofNullable(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}

