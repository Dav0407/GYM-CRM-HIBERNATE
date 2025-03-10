package com.epam.gym_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTrainerProfileRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String trainingTypeName;
}
