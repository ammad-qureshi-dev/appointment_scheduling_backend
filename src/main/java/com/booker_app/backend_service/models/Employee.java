package com.booker_app.backend_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Employee extends BaseEntity {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID generatedId;

    @Enumerated(EnumType.STRING)
    private EmploymentRole role;

    // Mappings
    @OneToOne
    private User user;

    @ManyToOne
    @ToString.Exclude
    private Company company;
}
