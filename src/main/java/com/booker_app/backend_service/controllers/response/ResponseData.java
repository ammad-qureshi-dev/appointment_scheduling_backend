/* (C) 2025 
Business Booking App. */
package com.booker_app.backend_service.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
	private String description;
	private ResponseType responseType;
	private ResponseSeverity responseSeverity;
}
