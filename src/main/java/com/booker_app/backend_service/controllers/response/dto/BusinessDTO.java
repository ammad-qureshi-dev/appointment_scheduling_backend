/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDTO {
	private String name;
	private String phoneNumber;
	private String email;
	private String description;
	private String address;
}
