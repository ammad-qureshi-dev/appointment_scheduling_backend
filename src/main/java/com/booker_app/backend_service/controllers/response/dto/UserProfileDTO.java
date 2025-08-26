/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
	private String label;
	private String role;
	private UUID contextId;
}
