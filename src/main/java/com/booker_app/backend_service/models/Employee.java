package com.booker_app.backend_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID employeeId;

    private String fullName;

    @ManyToOne
    private Company company;

    @Embedded
    private PhoneNumber phoneNumber;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;
}
