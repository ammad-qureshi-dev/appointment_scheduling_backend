/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import java.time.LocalDate;

import com.booker_app.backend_service.models.enums.ContactMethod;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommsRequest<T> {
	@NonNull private ContactMethod commsType;
	private T data;
	@NonNull private String recipient;
	@NonNull private String subject;
	private String messageContent;
	private LocalDate sendAt;
}
