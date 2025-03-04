package com.epam.gym_crm.service_impl;

import com.epam.gym_crm.dto.CreateTraineeProfileRequestDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TraineeRepository;
import com.epam.gym_crm.service.TraineeService;
import com.epam.gym_crm.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private static final Log LOG = LogFactory.getLog(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Trainee createTraineeProfile(CreateTraineeProfileRequestDTO request) {
        LOG.info("Creating new trainee profile for: " + request.getFirstName() + " " + request.getLastName());

        validateRequest(request);

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .username(userService.generateUsername(request.getFirstName(), request.getLastName()))
                .password(userService.generateRandomPassword())
                .isActive(true)
                .build();

        user = userService.saveUser(user);
        LOG.info("User created successfully: " + user.getUsername());

        Trainee trainee = Trainee.builder()
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress().trim())
                .user(user)
                .build();

        Trainee savedTrainee = traineeRepository.save(trainee)
                .orElseThrow(() -> new RuntimeException("Failed to save trainee"));

        LOG.info("Trainee profile created successfully: " + savedTrainee.getId());
        return savedTrainee;
    }

    @Override
    public Trainee getTraineeById(Long id) {
        LOG.info("Fetching trainee by ID: " + id);
        return traineeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));
    }

    private void validateRequest(CreateTraineeProfileRequestDTO request) {
        if (!StringUtils.hasText(request.getFirstName()) || !StringUtils.hasText(request.getLastName())) {
            throw new IllegalArgumentException("First name and last name cannot be empty");
        }
        if (!StringUtils.hasText(request.getAddress())) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        if (request.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }
    }
}
