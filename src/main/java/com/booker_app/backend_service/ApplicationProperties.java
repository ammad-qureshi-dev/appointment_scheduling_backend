/* (C) 2025 
Booker App. */
package com.booker_app.backend_service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "booking-service")
@Data
public class ApplicationProperties {
	public boolean secureCookies;
}
