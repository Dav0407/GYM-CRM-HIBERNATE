package com.epam.gym_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTrainerTrainingsRequestDTO {
    private String TrainerUsername;
    private String TraineeUsername;
    private Date from;
    private Date to;
}
