package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.dto.request.AddTrainingRequestDTO;
import com.epam.gym_crm.dto.request.GetTraineeTrainingsRequestDTO;
import com.epam.gym_crm.dto.request.GetTrainerTrainingsRequestDTO;
import com.epam.gym_crm.dto.response.TrainingResponseDTO;
import com.epam.gym_crm.entity.Training;
import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.repository.TrainingRepository;
import com.epam.gym_crm.service.TraineeService;
import com.epam.gym_crm.service.TraineeTrainerService;
import com.epam.gym_crm.service.TrainerService;
import com.epam.gym_crm.service.TrainingService;
import com.epam.gym_crm.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private static final Log LOG = LogFactory.getLog(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    private final TrainingTypeService trainingTypeService;
    private final TraineeTrainerService traineeTrainerService;

    @Override
    public List<TrainingResponseDTO> getTraineeTrainings(GetTraineeTrainingsRequestDTO request) {
        LOG.info("Fetching trainings for trainee: {}" + request.getTraineeUsername());

        // Validate trainee username
        String traineeUsername = Optional.ofNullable(request.getTraineeUsername())
                .map(String::trim)
                .filter(username -> !username.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Trainee username cannot be empty."));

        // Validate date range
        if (request.getFrom() != null && request.getTo() != null && request.getFrom().after(request.getTo())) {
            LOG.error("Invalid date range: 'from' date is after 'to' date.");
            throw new IllegalArgumentException("Invalid date range: 'from' date cannot be after 'to' date.");
        }

        // Validate trainer (if provided)
        String trainerUsername = Optional.ofNullable(request.getTrainerUsername())
                .map(String::trim)
                .orElse(null);

        if (trainerUsername != null && trainerService.getTrainerByUsername(trainerUsername) == null) {
            throw new IllegalArgumentException("Trainer not found with username: " + trainerUsername);
        }

        // Fetch trainings
        List<TrainingResponseDTO> trainings = trainingRepository.findAllTraineeTrainings(
                        traineeUsername, trainerUsername, request.getFrom(), request.getTo(), request.getTrainingType())
                .stream()
                .map(this::getTrainingDTO)
                .toList();

        LOG.info("Found " + trainings.size() + " trainings for trainee: " + traineeUsername);

        return trainings;
    }


    @Override
    public List<TrainingResponseDTO> getTrainerTrainings(GetTrainerTrainingsRequestDTO request) {
        LOG.info("Fetching trainings for trainer: {}" + request.getTrainerUsername());

        // Validate trainer username
        String trainerUsername = Optional.ofNullable(request.getTrainerUsername())
                .map(String::trim)
                .filter(username -> !username.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Trainer username cannot be empty."));

        // Validate date range
        if (request.getFrom() != null && request.getTo() != null && request.getFrom().after(request.getTo())) {
            LOG.error("Invalid date range: 'from' date is after 'to' date.");
            throw new IllegalArgumentException("Invalid date range: 'from' date cannot be after 'to' date.");
        }

        // Check if trainer exists
        if (trainerService.getTrainerByUsername(trainerUsername) == null) {
            throw new IllegalArgumentException("Trainer not found with username: " + trainerUsername);
        }

        // Validate trainee (if provided)
        String traineeUsername = Optional.ofNullable(request.getTraineeUsername())
                .map(String::trim)
                .orElse(null);

        if (traineeUsername != null && traineeService.getTraineeByUsername(traineeUsername) == null) {
            throw new IllegalArgumentException("Trainee not found with username: " + traineeUsername);
        }

        // Fetch trainings
        List<TrainingResponseDTO> trainings = trainingRepository.findAllTrainerTrainings(
                        trainerUsername, traineeUsername, request.getFrom(), request.getTo())
                .stream()
                .map(this::getTrainingDTO)
                .toList();

        LOG.info("Found " + trainings.size() + " trainings for trainer: " + trainerUsername);

        return trainings;
    }


    @Override
    public TrainingResponseDTO addTraining(AddTrainingRequestDTO request) {
        LOG.info("Adding new training...");

        // Validate input
        String traineeUsername = Optional.ofNullable(request.getTraineeUsername())
                .map(String::trim)
                .filter(username -> !username.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Trainee username cannot be empty."));

        String trainerUsername = Optional.ofNullable(request.getTrainerUsername())
                .map(String::trim)
                .filter(username -> !username.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Trainer username cannot be empty."));

        Date trainingDate = Optional.ofNullable(request.getTrainingDate())
                .orElseThrow(() -> new IllegalArgumentException("Training date cannot be null."));

        Integer trainingDuration = Optional.ofNullable(request.getTrainingDuration())
                .orElseThrow(() -> new IllegalArgumentException("Training duration cannot be null."));

        String trainingName = Optional.ofNullable(request.getTrainingName()).orElseThrow(() -> new IllegalArgumentException("Training name cannot be null."));

        // Fetch Trainee & Trainer
        var trainee = Optional.ofNullable(traineeService.getTraineeEntityByUsername(traineeUsername))
                .orElseThrow(() -> new IllegalArgumentException("No trainee found with username: " + traineeUsername));

        var trainer = Optional.ofNullable(trainerService.getTrainerEntityByUsername(trainerUsername))
                .orElseThrow(() -> new IllegalArgumentException("No trainer found with username: " + trainerUsername));

        // Fetch TrainingType
        TrainingType trainingType = trainingTypeService.findByValue(request.getTrainingTypeName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid training type: " + request.getTrainingTypeName()));

        // Create Training Object
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        training.setTrainingName(trainingName);

        // Save Training
        Training savedTraining = trainingRepository.save(training);
        LOG.info("Training added successfully with ID: " + savedTraining.getId());

        // Create Trainee-Trainer Relationship
        traineeTrainerService.createTraineeTrainer(traineeUsername, trainerUsername);
        LOG.info("Trainer-Trainee relation created successfully.");

        return getTrainingDTO(training);
    }

    private TrainingResponseDTO getTrainingDTO(Training training) {
        return TrainingResponseDTO.builder()
                .id(training.getId())
                .trainee(traineeService.getTraineeResponseDTO(training.getTrainee()))
                .trainer(trainerService.getTrainerResponseDTO(training.getTrainer()))
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType().getTrainingTypeName())
                .trainingDuration(training.getTrainingDuration())
                .trainingDate(training.getTrainingDate())
                .build();

    }
}
