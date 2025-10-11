/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import java.util.UUID;

import com.booker_app.backend_service.models.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private String fullName;
	private String email;
	private String phoneNumber;
	private UUID userId;
	private UserRole lastSignedInAs;
}
