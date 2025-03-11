package com.epam.gym_crm.dto.request;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;

class GetTrainerTrainingsRequestDTOTest {

    @Test
    void testNoArgsConstructor() {
        GetTrainerTrainingsRequestDTO dto = new GetTrainerTrainingsRequestDTO();
        assertThat(dto).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        Date fromDate = new Date();
        Date toDate = new Date();

        GetTrainerTrainingsRequestDTO dto = new GetTrainerTrainingsRequestDTO(
                "trainer_01",
                "john_doe",
                fromDate,
                toDate
        );

        assertThat(dto.getTrainerUsername()).isEqualTo("trainer_01");
        assertThat(dto.getTraineeUsername()).isEqualTo("john_doe");
        assertThat(dto.getFrom()).isEqualTo(fromDate);
        assertThat(dto.getTo()).isEqualTo(toDate);
    }

    @Test
    void testBuilder() {
        Date fromDate = new Date();
        Date toDate = new Date();

        GetTrainerTrainingsRequestDTO dto = GetTrainerTrainingsRequestDTO.builder()
                .TrainerUsername("trainer_02")
                .TraineeUsername("alice_smith")
                .from(fromDate)
                .to(toDate)
                .build();

        assertThat(dto.getTrainerUsername()).isEqualTo("trainer_02");
        assertThat(dto.getTraineeUsername()).isEqualTo("alice_smith");
        assertThat(dto.getFrom()).isEqualTo(fromDate);
        assertThat(dto.getTo()).isEqualTo(toDate);
    }

    @Test
    void testToString() {
        Date fromDate = new Date();
        Date toDate = new Date();

        GetTrainerTrainingsRequestDTO dto = GetTrainerTrainingsRequestDTO.builder()
                .TrainerUsername("trainer_x")
                .TraineeUsername("test_user")
                .from(fromDate)
                .to(toDate)
                .build();

        String dtoString = dto.toString();
        assertThat(dtoString).contains("TrainerUsername=trainer_x", "TraineeUsername=test_user");
    }
}
