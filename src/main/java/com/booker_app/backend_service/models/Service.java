package com.booker_app.backend_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Service extends BaseEntity {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String description;
    private Double price;

    // 1 HOUR, 25 MINUTES, etc
    private Integer time;
    private ServiceLength serviceLength;

    // Mappings
    @ManyToOne
    @ToString.Exclude
    private Company company;
}
