package com.booker_app.backend_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID generatedId;

    // Mappings
    @OneToOne
    private User user;

    @ManyToOne
    private Company company;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
}
