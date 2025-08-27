/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response.dto;

import java.math.BigDecimal;

import com.booker_app.backend_service.models.enums.ServiceLength;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
	private String name;
	private String description;
	private BigDecimal price;
	private Integer time;
	private ServiceLength serviceLength;
}
