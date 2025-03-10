package com.epam.gym_crm.service_test;

import com.epam.gym_crm.dto.request.CreateTraineeProfileRequestDTO;
import com.epam.gym_crm.dto.request.UpdateTraineeProfileRequestDTO;
import com.epam.gym_crm.dto.response.TraineeResponseDTO;
import com.epam.gym_crm.entity.Trainee;
import com.epam.gym_crm.entity.User;
import com.epam.gym_crm.repository.TraineeRepository;
import com.epam.gym_crm.service.UserService;
import com.epam.gym_crm.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private CreateTraineeProfileRequestDTO createRequest;
    private UpdateTraineeProfileRequestDTO updateRequest;
    private User user;
    private Trainee trainee;
    private Date dateOfBirth;

    @BeforeEach
    void setUp() {
        dateOfBirth = new Date(); // Use current date or a specific date for testing

        createRequest = new CreateTraineeProfileRequestDTO();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(dateOfBirth);
        createRequest.setAddress("123 Main St");

        updateRequest = new UpdateTraineeProfileRequestDTO();
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setUsername("jane.smith");
        updateRequest.setDateOfBirth(dateOfBirth);
        updateRequest.setAddress("456 Elm St");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("password");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress("123 Main St");
        trainee.setUser(user);
    }

    @Test
    void testCreateTraineeProfile() {
        when(userService.generateUsername(anyString(), anyString())).thenReturn("john.doe");
        when(userService.generateRandomPassword()).thenReturn("password");
        when(userService.saveUser(any(User.class))).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        TraineeResponseDTO response = traineeService.createTraineeProfile(createRequest);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe", response.getUsername());
        assertEquals("password", response.getPassword());
        assertTrue(response.getIsActive());
        assertEquals(dateOfBirth, response.getBirthDate());
        assertEquals("123 Main St", response.getAddress());

        verify(userService, times(1)).generateUsername("John", "Doe");
        verify(userService, times(1)).generateRandomPassword();
        verify(userService, times(1)).saveUser(any(User.class));
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testGetTraineeById() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        TraineeResponseDTO response = traineeService.getTraineeById(1L);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe", response.getUsername());
        assertEquals("password", response.getPassword());
        assertTrue(response.getIsActive());
        assertEquals(dateOfBirth, response.getBirthDate());
        assertEquals("123 Main St", response.getAddress());

        verify(traineeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTraineeById_NotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> traineeService.getTraineeById(1L));

        verify(traineeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTraineeByUsername() {
        when(userService.getUserByUsername("john.doe")).thenReturn(user);
        when(traineeRepository.findByUserId(1L)).thenReturn(Optional.of(trainee));

        TraineeResponseDTO response = traineeService.getTraineeByUsername("john.doe");

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe", response.getUsername());
        assertEquals("password", response.getPassword());
        assertTrue(response.getIsActive());
        assertEquals(dateOfBirth, response.getBirthDate());
        assertEquals("123 Main St", response.getAddress());

        verify(userService, times(1)).getUserByUsername("john.doe");
        verify(traineeRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetTraineeByUsername_NotFound() {
        when(userService.getUserByUsername("john.doe")).thenReturn(user);
        when(traineeRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> traineeService.getTraineeByUsername("john.doe"));

        verify(userService, times(1)).getUserByUsername("john.doe");
        verify(traineeRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetTraineeEntityByUsername() {
        when(userService.getUserByUsername("john.doe")).thenReturn(user);
        when(traineeRepository.findByUserId(1L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTraineeEntityByUsername("john.doe");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(dateOfBirth, result.getDateOfBirth());
        assertEquals("123 Main St", result.getAddress());
        assertEquals(user, result.getUser());

        verify(userService, times(1)).getUserByUsername("john.doe");
        verify(traineeRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetTraineeEntityByUsername_NotFound() {
        when(userService.getUserByUsername("john.doe")).thenReturn(user);
        when(traineeRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> traineeService.getTraineeEntityByUsername("john.doe"));

        verify(userService, times(1)).getUserByUsername("john.doe");
        verify(traineeRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testUpdateTraineeProfile() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        TraineeResponseDTO response = traineeService.updateTraineeProfile(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("jane.smith", response.getUsername());
        assertEquals(dateOfBirth, response.getBirthDate());
        assertEquals("456 Elm St", response.getAddress());

        verify(traineeRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateTraineeProfile_NotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> traineeService.updateTraineeProfile(1L, updateRequest));

        verify(traineeRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteTraineeProfileByUsername() {
        doNothing().when(userService).deleteUser("john.doe");

        traineeService.deleteTraineeProfileByUsername("john.doe");

        verify(userService, times(1)).deleteUser("john.doe");
    }
}