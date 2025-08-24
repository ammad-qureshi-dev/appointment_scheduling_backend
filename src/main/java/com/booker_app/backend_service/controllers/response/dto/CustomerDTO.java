/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import java.time.LocalDate;

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
	private String customerId;

	public CustomerDTO(String fullName, String phoneNumber, String email) {
		this.fullName = fullName;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public CustomerDTO(String fullName, String phoneNumber, String email, String customerId) {
		this.fullName = fullName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.customerId = customerId;
	}
}
