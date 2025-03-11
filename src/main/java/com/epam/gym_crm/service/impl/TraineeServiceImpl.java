package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.dto.request.CreateTraineeProfileRequestDTO;
import com.epam.gym_crm.dto.request.UpdateTraineeProfileRequestDTO;
import com.epam.gym_crm.dto.response.TraineeResponseDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TraineeRepository;
import com.epam.gym_crm.service.TraineeService;
import com.epam.gym_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private static final Log LOG = LogFactory.getLog(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final UserService userService;

    @Transactional
    @Override
    public TraineeResponseDTO createTraineeProfile(CreateTraineeProfileRequestDTO request) {
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
        LOG.info("User created successfully: " + user.toString());

        Trainee trainee = Trainee.builder()
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress().trim())
                .user(user)
                .build();

        Trainee savedTrainee = traineeRepository.save(trainee);

        LOG.info("Trainee profile created successfully: " + savedTrainee.toString());
        return getTraineeResponseDTO(trainee);
    }

    @Override
    public TraineeResponseDTO getTraineeById(Long id) {

        LOG.info("Fetching trainee by ID: " + id);

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));

        return getTraineeResponseDTO(trainee);
    }

    @Override
    public TraineeResponseDTO getTraineeByUsername(String username) {

        User userByUsername = userService.getUserByUsername(username);

        Trainee trainee = traineeRepository.findByUserId(userByUsername.getId())
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + userByUsername.getUsername()));

        return getTraineeResponseDTO(trainee);
    }

    @Override
    public Trainee getTraineeEntityByUsername(String username) {
        User userByUsername = userService.getUserByUsername(username);

        return traineeRepository.findByUserId(userByUsername.getId())
                .orElseThrow(() -> new RuntimeException("Trainee not found with username: " + userByUsername.getUsername()));
    }

    @Transactional
    @Override
    public TraineeResponseDTO updateTraineeProfile(Long id, UpdateTraineeProfileRequestDTO request) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found with ID: " + id));

        trainee.getUser().setFirstName(request.getFirstName().trim());
        trainee.getUser().setLastName(request.getLastName().trim());
        trainee.getUser().setUsername(request.getUsername().trim());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress().trim());

        return getTraineeResponseDTO(trainee);
    }

    @Override
    public void updateStatus(String username) {
        userService.updateStatus(username);
    }

    @Override
    public void deleteTraineeProfileByUsername(String username) {
        userService.deleteUser(username);
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

    public TraineeResponseDTO getTraineeResponseDTO(Trainee trainee) {
        return TraineeResponseDTO.builder()
                .id(trainee.getId())
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .username(trainee.getUser().getUsername())
                .password(trainee.getUser().getPassword())
                .isActive(trainee.getUser().getIsActive())
                .birthDate(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .build();
    }
}
