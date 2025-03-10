package com.epam.gym_crm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResponseDTO {
    private Long id;
    private TraineeResponseDTO trainee;
    private TrainerResponseDTO trainer;
    private String trainingName;
    private String trainingType;
    private Date trainingDate;
    private Integer trainingDuration;
}
