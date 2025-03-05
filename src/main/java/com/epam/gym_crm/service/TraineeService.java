package com.epam.gym_crm.service;

import com.epam.gym_crm.dto.CreateTraineeProfileRequestDTO;
import com.epam.gym_crm.dto.UpdateTraineeProfileRequestDTO;
import com.epam.gym_crm.entity.Trainee;

public interface TraineeService {
    Trainee createTraineeProfile(CreateTraineeProfileRequestDTO request);
    Trainee getTraineeById(Long id);
    Trainee getTraineeByUsername(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainee updateTraineeProfile(Long id, UpdateTraineeProfileRequestDTO request);
    void updateStatus(String username);
    void deleteTraineeProfileByUsername(String username);
}
