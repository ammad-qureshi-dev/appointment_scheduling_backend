/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOverviewDTO {
	private List<ServiceOverviewDTO> serviceOverview;
	private BigDecimal totalSpend;
	private BigDecimal avgSpend;
	private LocalDate customerSince;
	private AppointmentDTO upcomingAppointment;
	private String customerId;
}
