/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.booker_app.backend_service.models.AppointmentStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
	@NonNull private List<String> services;
	@NonNull private LocalDateTime startTime;
	@NonNull private LocalDateTime endTime;
	@NonNull private LocalDate appointmentDate;
	@NonNull private AppointmentStatus status;
}
