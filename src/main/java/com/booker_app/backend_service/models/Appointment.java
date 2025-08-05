package com.booker_app.backend_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointment", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseEntity {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String services;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate appointmentDate;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private AppointmentStatus appointmentStatus = AppointmentStatus.PENDING;

    // Mappings
    @ManyToOne
    @ToString.Exclude
    private Company company;

    @ManyToOne
    @ToString.Exclude
    private Customer customer;

    @OneToOne
    private Employee assignee;
}


