/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import com.booker_app.backend_service.models.enums.AuthMethod;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	private String email;
	private String phoneNumber;
	@NonNull private String password;
	private AuthMethod loginMethod;
}
