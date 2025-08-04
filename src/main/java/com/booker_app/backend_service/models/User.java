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
@Table(name = "user", schema = "booker_app")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NonNull
    private String fullName;

    @Embedded
    @NonNull
    private PhoneNumber phoneNumber;

    @NonNull
    private String email;
    private LocalDate dateOfBirth;

    // Mappings
}
