package com.epam.gym_crm.service_test;

import com.epam.gym_crm.entity.TrainingType;
import com.epam.gym_crm.repository.TrainingTypeRepository;
import com.epam.gym_crm.service.impl.TrainingTypeServiceImpl;
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

        String value = "Cardio";
        TrainingType expectedTrainingType = new TrainingType();
        expectedTrainingType.setTrainingTypeName(value);

        when(trainingTypeRepository.findByValue(value)).thenReturn(Optional.of(expectedTrainingType));

        Optional<TrainingType> result = trainingTypeService.findByValue(value);

        assertTrue(result.isPresent());
        assertEquals(value, result.get().getTrainingTypeName());
        verify(trainingTypeRepository).findByValue(value);
    }

    @Test
    void findByValue_WhenTrainingTypeDoesNotExist_ReturnsEmptyOptional() {

        String value = "InvalidType";

        when(trainingTypeRepository.findByValue(value)).thenReturn(Optional.empty());

        Optional<TrainingType> result = trainingTypeService.findByValue(value);

        assertFalse(result.isPresent());
        verify(trainingTypeRepository).findByValue(value);
    }
}