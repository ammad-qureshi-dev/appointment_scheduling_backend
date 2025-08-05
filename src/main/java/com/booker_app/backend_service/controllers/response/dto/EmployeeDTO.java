package com.booker_app.backend_service.controllers.response.dto;

import com.booker_app.backend_service.models.EmploymentRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    @Enumerated(EnumType.STRING)
    private EmploymentRole role;
    private String fullName;
    private String phoneNumber;
    private String email;
    private LocalDate dateOfBirth;
    private UUID employeeId;
}
