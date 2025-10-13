/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommsResponse {
	@NonNull private UUID responseId;

	private boolean sentStatus;

	@NonNull private LocalDate sentAt;

	@NonNull private String recipient;

	@NonNull private String subject;
}
