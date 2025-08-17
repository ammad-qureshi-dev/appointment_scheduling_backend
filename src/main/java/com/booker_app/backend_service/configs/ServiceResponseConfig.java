/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.configs;

import java.util.ArrayList;

import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class ServiceResponseConfig {

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ServiceResponse<?> getServiceResponse() {
		var alerts = new ArrayList<ResponseData>();
		return ServiceResponse.builder().alerts(alerts).build();
	}
}
