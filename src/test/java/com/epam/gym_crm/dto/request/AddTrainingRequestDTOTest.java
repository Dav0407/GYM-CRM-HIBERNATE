package com.epam.gym_crm.dto.request;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

class AddTrainingRequestDTOTest {

    @Test
    void testNoArgsConstructor() {
        AddTrainingRequestDTO dto = new AddTrainingRequestDTO();
        assertThat(dto).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        Date trainingDate = new Date();
        AddTrainingRequestDTO dto = new AddTrainingRequestDTO(
                "trainee123",
                "trainer456",
                "Advanced Java",
                "Programming",
                trainingDate,
                120
        );

        assertThat(dto.getTraineeUsername()).isEqualTo("trainee123");
        assertThat(dto.getTrainerUsername()).isEqualTo("trainer456");
        assertThat(dto.getTrainingName()).isEqualTo("Advanced Java");
        assertThat(dto.getTrainingTypeName()).isEqualTo("Programming");
        assertThat(dto.getTrainingDate()).isEqualTo(trainingDate);
        assertThat(dto.getTrainingDuration()).isEqualTo(120);
    }

    @Test
    void testBuilder() {
        Date trainingDate = new Date();
        AddTrainingRequestDTO dto = AddTrainingRequestDTO.builder()
                .traineeUsername("user1")
                .trainerUsername("trainerX")
                .trainingName("Machine Learning Basics")
                .trainingTypeName("AI")
                .trainingDate(trainingDate)
                .trainingDuration(90)
                .build();

        assertThat(dto.getTraineeUsername()).isEqualTo("user1");
        assertThat(dto.getTrainerUsername()).isEqualTo("trainerX");
        assertThat(dto.getTrainingName()).isEqualTo("Machine Learning Basics");
        assertThat(dto.getTrainingTypeName()).isEqualTo("AI");
        assertThat(dto.getTrainingDate()).isEqualTo(trainingDate);
        assertThat(dto.getTrainingDuration()).isEqualTo(90);
    }

    @Test
    void testToString() {
        AddTrainingRequestDTO dto = AddTrainingRequestDTO.builder()
                .traineeUsername("testUser")
                .trainerUsername("testTrainer")
                .trainingName("Spring Boot Training")
                .build();

        String dtoString = dto.toString();
        assertThat(dtoString).contains("traineeUsername=testUser", "trainerUsername=testTrainer", "trainingName=Spring Boot Training");
    }
}
