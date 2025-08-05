/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRegistrationRequest {

	@NonNull private String fullName;

	@NonNull private String email;

	@NonNull private String password;

	private LocalDate dateOfBirth;
	private String phoneNumber;
}
