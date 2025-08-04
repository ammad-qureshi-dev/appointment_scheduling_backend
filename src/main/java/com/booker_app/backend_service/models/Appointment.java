package com.booker_app.backend_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointment", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID appointmentId;

    @ManyToOne
    private Company company;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private List<Service> services;

    @OneToOne
    private Customer customer;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
