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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID companyId;

    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Employee> employees;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<AppointmentService> appointmentServices;

    @Embedded
    private PhoneNumber phoneNumber;

    private String email;

}
