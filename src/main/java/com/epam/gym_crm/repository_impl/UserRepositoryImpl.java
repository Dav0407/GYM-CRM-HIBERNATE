package com.epam.gym_crm.repository_impl;

import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public User save(User user) {
        try {
            if (user.getId() == null || !entityManager.contains(user)) {
                entityManager.persist(user);
                return user;
            } else {
                return entityManager.merge(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user: " + user, e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        User user = entityManager.find(User.class, username);
        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Transactional
    @Override
    public void deleteByUsername(String username) {
        User user = entityManager.find(User.class, username);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    @Transactional
    @Override
    public void updatePassword(String username, String newPassword) {
        User user = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        user.setPassword(newPassword);
        entityManager.merge(user);
    }
}
