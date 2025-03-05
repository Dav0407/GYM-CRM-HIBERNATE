package com.epam.gym_crm.service_test;

import com.epam.gym_crm.dto.CreateTraineeProfileRequestDTO;
import com.epam.gym_crm.dto.UpdateTraineeProfileRequestDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TraineeRepository;
import com.epam.gym_crm.service.UserService;
import com.epam.gym_crm.service_impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private CreateTraineeProfileRequestDTO createRequest;
    private User mockUser;
    private Trainee mockTrainee;

    @BeforeEach
    void setUp() {
        createRequest = new CreateTraineeProfileRequestDTO();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setAddress("123 Test Street");

        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.JANUARY, 1);
        createRequest.setDateOfBirth(calendar.getTime());

        mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .build();

        mockTrainee = Trainee.builder()
                .id(1L)
                .user(mockUser)
                .address("123 Test Street")
                .dateOfBirth(calendar.getTime())
                .build();
    }

    @Test
    void createTraineeProfile_ValidRequest_ShouldCreateSuccessfully() {
        // Arrange
        when(userService.generateUsername(createRequest.getFirstName(), createRequest.getLastName()))
                .thenReturn("john.doe");
        when(userService.generateRandomPassword()).thenReturn("password123");
        when(userService.saveUser(any(User.class))).thenReturn(mockUser);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(mockTrainee);

        // Act
        Trainee createdTrainee = traineeService.createTraineeProfile(createRequest);

        // Assert
        assertNotNull(createdTrainee);
        assertEquals(mockTrainee.getId(), createdTrainee.getId());
        verify(userService).generateUsername(createRequest.getFirstName(), createRequest.getLastName());
        verify(userService).saveUser(any(User.class));
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void createTraineeProfile_EmptyFirstName_ShouldThrowException() {
        // Arrange
        createRequest.setFirstName("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.createTraineeProfile(createRequest),
                "First name and last name cannot be empty");
    }

    @Test
    void getTraineeById_ExistingTrainee_ShouldReturnTrainee() {
        // Arrange
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(mockTrainee));

        // Act
        Trainee foundTrainee = traineeService.getTraineeById(1L);

        // Assert
        assertNotNull(foundTrainee);
        assertEquals(mockTrainee.getId(), foundTrainee.getId());
    }

    @Test
    void getTraineeById_NonExistingTrainee_ShouldThrowException() {
        // Arrange
        when(traineeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> traineeService.getTraineeById(999L),
                "Trainee not found with ID: 999");
    }

    @Test
    void getTraineeByUsername_ExistingTrainee_ShouldReturnTrainee() {
        // Arrange
        when(userService.getUserByUsername("john.doe")).thenReturn(mockUser);
        when(traineeRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockTrainee));

        // Act
        Trainee foundTrainee = traineeService.getTraineeByUsername("john.doe");

        // Assert
        assertNotNull(foundTrainee);
        assertEquals(mockTrainee.getId(), foundTrainee.getId());
    }

    @Test
    void updateTraineeProfile_ValidUpdate_ShouldUpdateSuccessfully() {
        // Arrange
        UpdateTraineeProfileRequestDTO updateRequest = new UpdateTraineeProfileRequestDTO();
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setUsername("jane.smith");
        updateRequest.setAddress("456 Update Street");

        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.JANUARY, 1);
        updateRequest.setDateOfBirth(calendar.getTime());

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(mockTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(mockTrainee);

        // Act
        Trainee updatedTrainee = traineeService.updateTraineeProfile(1L, updateRequest);

        // Assert
        assertNotNull(updatedTrainee);
        assertEquals("Jane", updatedTrainee.getUser().getFirstName());
        assertEquals("Smith", updatedTrainee.getUser().getLastName());
        assertEquals("jane.smith", updatedTrainee.getUser().getUsername());
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void deleteTraineeProfileByUsername_ShouldCallUserServiceDelete() {
        // Act
        traineeService.deleteTraineeProfileByUsername("john.doe");

        // Assert
        verify(userService).deleteUser("john.doe");
    }

    @Test
    void changePassword_ShouldCallUserServiceChangePassword() {
        // Act
        traineeService.changePassword("john.doe", "oldPassword", "newPassword");

        // Assert
        verify(userService).changePassword("john.doe", "oldPassword", "newPassword");
    }

    @Test
    void updateStatus_ShouldCallUserServiceUpdateStatus() {
        // Act
        traineeService.updateStatus("john.doe");

        // Assert
        verify(userService).updateStatus("john.doe");
    }
}