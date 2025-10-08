/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import com.booker_app.backend_service.controllers.response.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/v1/health")
public class HealthController {

	@GetMapping("/ping")
	public ResponseEntity<ServiceResponse<String>> ping() {
		return getServiceResponse(true, "pong", HttpStatus.OK);
	}
}
