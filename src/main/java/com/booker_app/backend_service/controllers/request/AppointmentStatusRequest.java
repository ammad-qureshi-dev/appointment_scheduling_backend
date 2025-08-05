/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import com.booker_app.backend_service.models.AppointmentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusRequest {
	@Enumerated(EnumType.STRING)
	private AppointmentStatus status;
}
