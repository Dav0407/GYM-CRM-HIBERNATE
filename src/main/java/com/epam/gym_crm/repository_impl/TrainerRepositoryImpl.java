package com.epam.gym_crm.repository_impl;

import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.repository.TrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Optional<Trainer> save(Trainer trainer) {
        try {
            if (trainer.getId() == null) {
                entityManager.persist(trainer);
            } else {
                trainer = entityManager.merge(trainer);
            }
            return Optional.of(trainer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save Trainer: " + trainer, e);
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Trainer trainer = entityManager.find(Trainer.class, id);
        return Optional.ofNullable(trainer);
    }

    @Override
    public List<Trainer> findAll() {
        return entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)
                .getResultList();
    }
}

