package com.epam.gym_crm.service_impl;

import com.epam.gym_crm.dto.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.TraineeTrainer;
import com.epam.gym_crm.entity.Trainer;
import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TrainingRepository;
import com.epam.gym_crm.service.TraineeTrainerService;
import com.epam.gym_crm.service.TrainingService;
import com.epam.gym_crm.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private static final Log LOG = LogFactory.getLog(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;

    private final TrainingTypeService trainingTypeService;
    private final TraineeTrainerService traineeTrainerService;

    @Override
    public List<Training> getTraineeTrainings(GetTraineeTrainingsRequestDTO request) {
        LOG.info("Fetching trainee trainings for: " + request.getTraineeUsername());

        // Input validation
        if (request.getTraineeUsername() == null || request.getTraineeUsername().trim().isEmpty()) {
            LOG.error("Invalid request: Trainee username is missing.");
            throw new IllegalArgumentException("Trainee username cannot be empty.");
        }

        if (request.getFrom() != null && request.getTo() != null && request.getFrom().after(request.getTo())) {
            LOG.error("Invalid date range: 'from' date is after 'to' date.");
            throw new IllegalArgumentException("Invalid date range: 'from' date cannot be after 'to' date.");
        }

        List<Training> trainings = trainingRepository.findAllTraineeTrainings(
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getFrom(),
                request.getTo(),
                request.getTrainingType());

        if (trainings.isEmpty()) {
            LOG.warn("No trainings found for trainee: " + request.getTraineeUsername());
        } else {
            LOG.info("Found " + trainings.size() + " trainings for trainee: " + request.getTraineeUsername());
        }

        return trainings;
    }

    @Override
    public List<Training> getTrainerTrainings(GetTrainerTrainingsRequestDTO request) {
        LOG.info("Fetching trainer trainings for: " + request.getTrainerUsername());

        // Input validation
        if (request.getTrainerUsername() == null || request.getTrainerUsername().trim().isEmpty()) {
            LOG.error("Invalid request: Trainer username is missing.");
            throw new IllegalArgumentException("Trainer username cannot be empty.");
        }

        if (request.getFrom() != null && request.getTo() != null && request.getFrom().after(request.getTo())) {
            LOG.error("Invalid date range: 'from' date is after 'to' date.");
            throw new IllegalArgumentException("Invalid date range: 'from' date cannot be after 'to' date.");
        }

        List<Training> trainings = trainingRepository.findAllTrainerTrainings(
                request.getTrainerUsername(),
                request.getTraineeUsername(),
                request.getFrom(),
                request.getTo());

        if (trainings.isEmpty()) {
            LOG.warn("No trainings found for trainer: " + request.getTrainerUsername());
        } else {
            LOG.info("Found " + trainings.size() + " trainings for trainer: " + request.getTrainerUsername());
        }

        return trainings;
    }

    @Override
    public Training addTraining(AddTrainingRequestDTO request) {
        LOG.info("Adding new training...");

        // Input validation
        if (request.getTraineeUsername() == null || request.getTraineeUsername().trim().isEmpty()) {
            LOG.error("Invalid request: Trainee username is missing.");
            throw new IllegalArgumentException("Trainee username cannot be empty.");
        }

        if (request.getTrainerUsername() == null || request.getTrainerUsername().trim().isEmpty()) {
            LOG.error("Invalid request: Trainer username is missing.");
            throw new IllegalArgumentException("Trainer username cannot be empty.");
        }

        if (request.getTrainingTypeName() == null) {
            LOG.error("Invalid request: Training type is missing.");
            throw new IllegalArgumentException("Training type cannot be null.");
        }

        if (request.getTrainingDate() == null) {
            LOG.error("Invalid request: Training date is missing.");
            throw new IllegalArgumentException("Training date cannot be null.");
        }

        // Initialize Trainee and Trainer
        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setUsername(request.getTraineeUsername().trim()); // Trim the username
        trainee.setUser(traineeUser);

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername(request.getTrainerUsername().trim()); // Trim the username
        trainer.setUser(trainerUser);

        // Fetch TrainingType
        TrainingType trainingType = trainingTypeService.findByValue(request.getTrainingTypeName())
                .orElseThrow(() -> {
                    LOG.error("Invalid training type: " + request.getTrainingTypeName());
                    return new IllegalArgumentException("Invalid training type: " + request.getTrainingTypeName());
                });

        // Initialize Training
        Training training = new Training();
        training.setTrainee(trainee); // Initialize trainee
        training.setTrainer(trainer); // Initialize trainer
        training.setTrainingType(trainingType);
        training.setTrainingDate(request.getTrainingDate());

        // Save the training
        Training savedTraining = trainingRepository.save(training);
        LOG.info("Training added successfully with ID: " + savedTraining.getId());

        // Create the Trainee-Trainer relationship
        TraineeTrainer traineeTrainer = traineeTrainerService.createTraineeTrainer(
                traineeUser.getUsername(),
                trainerUser.getUsername()
        );

        LOG.info("Trainer and Trainee relation added successfully " + traineeTrainer.getId());
        return savedTraining;
    }
}
