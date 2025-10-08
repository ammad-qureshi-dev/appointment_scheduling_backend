/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
	private String token;
	private UUID userId;
}
