package com.epam.gym_crm.repository;

import com.epam.gym_crm.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);
    List<User> findAll();
    void updatePassword(String username, String newPassword);
}
