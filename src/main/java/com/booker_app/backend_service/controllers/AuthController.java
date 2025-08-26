/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.LoginRequest;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.UserRole;
import com.booker_app.backend_service.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@RestController
@RequestMapping(BASE_URL_V1 + "/auth")
public class AuthController {

	private final AuthService authService;
	private final ServiceResponse<?> serviceResponse;

	public AuthController(AuthService authService, ServiceResponse<?> serviceResponse) {
		this.authService = authService;
		this.serviceResponse = serviceResponse;
	}

	@PostMapping("/login")
	private ResponseEntity<ServiceResponse<UUID>> login(@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {
		try {
			var userId = authService.userLogin(loginRequest, response);
			return getServiceResponse(true, userId, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			var alerts = serviceResponse.getAlerts();
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}

	@PostMapping("/switch-role/{userId}/{role}")
	private ResponseEntity<ServiceResponse<UUID>> switchRole(@PathVariable UUID userId, HttpServletResponse response,
			@PathVariable UserRole role) {
		try {
			authService.switchUserRole(userId, response, role);
			return getServiceResponse(true, userId, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			var alerts = serviceResponse.getAlerts();
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}

	@GetMapping("/user-profiles/{userId}")
	private ResponseEntity<ServiceResponse<List<UserProfileDTO>>> getUserProfiles(@PathVariable UUID userId) {
		try {
			var profiles = authService.getUserProfiles(userId);
			return getServiceResponse(true, profiles, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			var alerts = serviceResponse.getAlerts();
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.WARNING));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}

}
