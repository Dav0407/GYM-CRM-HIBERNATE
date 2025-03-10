package com.epam.gym_crm.dto.request;

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
public class UpdateTraineeProfileRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private Date dateOfBirth;
    private String address;
}
