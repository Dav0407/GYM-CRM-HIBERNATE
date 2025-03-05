package com.epam.gym_crm.service_test;

import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.repository.TrainingTypeRepository;
import com.epam.gym_crm.service_impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Test
    void findByValue_WhenTrainingTypeExists_ReturnsTrainingType() {
        // Arrange
        String value = "Cardio";
        TrainingType expectedTrainingType = new TrainingType();
        expectedTrainingType.setTrainingTypeName(value);

        when(trainingTypeRepository.findByValue(value)).thenReturn(Optional.of(expectedTrainingType));

        // Act
        Optional<TrainingType> result = trainingTypeService.findByValue(value);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(value, result.get().getTrainingTypeName());
        verify(trainingTypeRepository).findByValue(value);
    }

    @Test
    void findByValue_WhenTrainingTypeDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        String value = "InvalidType";

        when(trainingTypeRepository.findByValue(value)).thenReturn(Optional.empty());

        // Act
        Optional<TrainingType> result = trainingTypeService.findByValue(value);

        // Assert
        assertFalse(result.isPresent());
        verify(trainingTypeRepository).findByValue(value);
    }
}