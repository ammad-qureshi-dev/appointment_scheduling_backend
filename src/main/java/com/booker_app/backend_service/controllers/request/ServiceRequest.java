/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import java.math.BigDecimal;

import com.booker_app.backend_service.models.ServiceLength;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
	@NonNull private String name;
	private String description;
	@NonNull private BigDecimal price;
	@NonNull private Integer time;
	@NonNull private ServiceLength serviceLength;
}
