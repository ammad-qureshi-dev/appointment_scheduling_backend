/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import com.booker_app.backend_service.models.enums.EmploymentRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {
	@NonNull private String email;

	@Enumerated(EnumType.STRING)
	private EmploymentRole role;
}
