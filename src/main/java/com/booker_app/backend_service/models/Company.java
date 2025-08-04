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

    @Embedded
    private PhoneNumber phoneNumber;
    private String email;
    private String description;
    private String address;

    // Mappings
    @OneToOne
    private Employee owner;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Employee> employees;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Customer> customers;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Service> services;

}
