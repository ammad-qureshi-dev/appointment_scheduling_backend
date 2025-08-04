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
@Table(name = "company", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class Company extends BaseEntity{

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String phoneNumber;
    private String email;
    private String description;
    private String address;

    // Mappings
    @ToString.Exclude
    @OneToOne
    private Employee owner;

    @ToString.Exclude
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Customer> customers;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services;

}
