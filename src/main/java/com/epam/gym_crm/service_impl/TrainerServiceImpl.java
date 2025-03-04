package com.epam.gym_crm.service_impl;

import com.epam.gym_crm.dto.CreateTrainerProfileRequestDTO;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TrainerRepository;
import com.epam.gym_crm.repository.TrainingTypeRepository;
import com.epam.gym_crm.service.TrainerService;
import com.epam.gym_crm.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final Log LOG = LogFactory.getLog(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Trainer createTrainerProfile( CreateTrainerProfileRequestDTO request) {
        LOG.info("Creating new trainer profile for: " + request.getFirstName() + " " + request.getLastName());

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

        Trainer trainer = Trainer.builder()
                .specialization(trainingTypeRepository.findByValue(request.getTrainingType())
                        .orElseThrow(() -> new RuntimeException("Training type not found: " + request.getTrainingType())))
                .user(user)
                .build();

        Trainer savedTrainer = trainerRepository.save(trainer)
                .orElseThrow(() -> new RuntimeException("Failed to save trainer"));

        LOG.info("Trainer profile created successfully: " + savedTrainer.getId());
        return savedTrainer;
    }

    @Override
    public Trainer getTrainerById(Long id) {
        LOG.info("Fetching trainer by ID: " + id);
        return trainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + id));
    }

    private void validateRequest(CreateTrainerProfileRequestDTO request) {
        if (!StringUtils.hasText(request.getFirstName()) || !StringUtils.hasText(request.getLastName())) {
            throw new IllegalArgumentException("First name and last name cannot be empty");
        }
        if (!StringUtils.hasText(request.getTrainingType())) {
            throw new IllegalArgumentException("Training type cannot be empty");
        }
    }
}
