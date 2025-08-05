/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
	private String fullName;
	private String phoneNumber;
	private String email;
	private LocalDate dateOfBirth;
	private UUID customerId;
}
