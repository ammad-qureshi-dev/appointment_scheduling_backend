package com.booker_app.backend_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointment_service", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Service extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID appointmentServiceId;

    @ManyToOne
    private Appointment appointment;

    @ManyToOne
    private Company company;

    private String serviceType;
    private Double serviceCost;
    private Double serviceLength;
}
