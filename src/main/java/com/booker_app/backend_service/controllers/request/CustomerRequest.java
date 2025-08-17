/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
	@NonNull private String fullName;
	private String email;
	private String phoneNumber;
}
