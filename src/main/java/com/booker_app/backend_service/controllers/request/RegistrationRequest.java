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
public class RegistrationRequest {

	// Password should be encrypted prior to reaching BE layer
	@NonNull private String password;
	@NonNull private String fullName;
	private String email;
	private LocalDate dateOfBirth;
	private String phoneNumber;
}
