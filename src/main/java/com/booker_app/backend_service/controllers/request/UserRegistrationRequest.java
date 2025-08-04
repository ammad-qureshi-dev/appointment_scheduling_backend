package com.booker_app.backend_service.controllers.request;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRegistrationRequest {

    @NonNull
    private String fullName;

    @NonNull
    private String email;

    @NonNull
    private String password;

    private LocalDate dateOfBirth;
    private String phoneNumber;
}
