/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
	private String email;
	private String phoneNumber;
}
