package com.epam.gym_crm.service_impl;

import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.UserRepository;
import com.epam.gym_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public User saveUser(User user) {
        LOG.info("Saving user: " + user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        boolean exists = userRepository.findByUsername(username).isPresent();
        LOG.info("Checking if username exists (" + username + "): " + exists);
        return exists;
    }

    @Override
    public boolean isPasswordValid(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    boolean valid = user.getPassword().equals(password);
                    LOG.info("Password validation for " + username + ": " + valid);
                    return valid;
                })
                .orElseGet(() -> {
                    LOG.warn("Username not found: " + username);
                    return false;
                });
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            if (user.getPassword().equals(oldPassword)) {
                userRepository.updatePassword(username, newPassword);
                LOG.info("Password successfully changed for " + username);
            } else {
                LOG.error("Old password does not match for " + username);
                throw new IllegalArgumentException("Old password is incorrect.");
            }
        }, () -> {
            LOG.warn("User not found: " + username);
            throw new RuntimeException("User not found.");
        });
    }

    @Override
    public String generateUsername(String firstName, String lastName) {
        if (!StringUtils.hasText(firstName) || !StringUtils.hasText(lastName)) {
            throw new IllegalArgumentException("First name and last name cannot be empty.");
        }

        LOG.info("Generating username for: " + firstName + " " + lastName);

        String baseUsername = firstName.trim().toLowerCase() + "." + lastName.trim().toLowerCase();
        String username = baseUsername;
        int suffix = 1;

        while (checkUsernameExists(username)) {
            LOG.warn("Username conflict: " + username + " already exists. Trying next.");
            username = baseUsername + suffix;
            suffix++;
        }

        LOG.info("Generated unique username: " + username);
        return username;
    }

    @Override
    public String generateRandomPassword() {
        String password = RANDOM.ints(10, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());

        LOG.info("Generated a random password.");
        return password;
    }

    @Override
    public void updateStatus(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            boolean newStatus = !user.getIsActive();
            user.setIsActive(newStatus);
            userRepository.save(user);
            LOG.info("User status updated: " + username + " -> isActive: " + newStatus);
        }, () -> {
            LOG.warn("User not found: " + username);
            throw new RuntimeException("User not found.");
        });
    }

    @Override
    public void deleteUser(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            userRepository.deleteByUsername(username);
            LOG.info("User deleted successfully: " + username);
        }, () -> {
            LOG.warn("Attempted to delete non-existent user: " + username);
            throw new RuntimeException("User not found.");
        });
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found."));
    }
}
