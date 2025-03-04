package com.epam.gym_crm.service;

import com.epam.gym_crm.entity.User;

public interface UserService {
    String generateUsername(String firstName, String lastName);

    String generateRandomPassword();

    User saveUser(User user);
    boolean checkUsernameExists(String username);

    boolean isPasswordValid(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void updateStatus(String username);

    void deleteUser(String username);
}
