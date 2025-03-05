package com.epam.gym_crm.repository_impl;

import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.repository.TraineeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Trainee save(Trainee trainee) {
        try {
            if (trainee.getId() == null || !entityManager.contains(trainee)) {
                entityManager.persist(trainee);
                return trainee;
            } else {
                return entityManager.merge(trainee);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save trainee: " + trainee, e);
        }
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Trainee trainee = entityManager.find(Trainee.class, id);
        return Optional.ofNullable(trainee);
    }

    @Override
    public Optional<Trainee> findByUserId(Long userId) {
        return entityManager.createQuery("SELECT t FROM Trainee t WHERE t.user.id = :userId", Trainee.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }

}
