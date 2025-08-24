/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.models.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
	private List<String> services;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private LocalDate appointmentDate;
	private CustomerDTO customer;
	private AppointmentStatus appointmentStatus;
	private UUID appointmentId;
	private String assignedTo;
	private BigDecimal appointmentCost;
}
