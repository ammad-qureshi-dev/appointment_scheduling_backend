/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewBusinessRequest {
	@NonNull private String name;
	@NonNull private String address;
	private String description;
}
