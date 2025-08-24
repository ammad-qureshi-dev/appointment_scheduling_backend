/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOverviewDTO {
	private String name;
	private Long numberOfTimes;
	private BigDecimal price;
	private BigDecimal total;
}
