/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.LoginRequest;
import com.booker_app.backend_service.controllers.request.RegistrationRequest;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.enums.AccountVerificationMethod;
import com.booker_app.backend_service.models.enums.UserRole;
import com.booker_app.backend_service.services.AuthService;
import com.booker_app.backend_service.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/v1/auth")
public class AuthController {

	private final AuthService authService;
	private final ServiceResponse<?> serviceResponse;
	private final UserService userService;
	private static final String NO_TOKEN_COOKIE_HEADER = "token=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None";

	public AuthController(AuthService authService, ServiceResponse<?> serviceResponse, UserService userService) {
		this.authService = authService;
		this.serviceResponse = serviceResponse;
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<ServiceResponse<UUID>> register(@RequestBody RegistrationRequest request) {

		var alerts = serviceResponse.getAlerts();
		var headers = new HttpHeaders();

		try {
			var response = authService.registerV2(request);

			var cookie = authService.setCookie(response.getToken());

			headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

			return getServiceResponse(true, response.getUserId(), HttpStatus.OK, headers, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			headers.add(HttpHeaders.SET_COOKIE, NO_TOKEN_COOKIE_HEADER);
		}

		return getServiceResponse(true, null, HttpStatus.INTERNAL_SERVER_ERROR, headers, alerts);
	}

	@PostMapping("/login")
	public ResponseEntity<ServiceResponse<UUID>> login(@RequestBody LoginRequest request) {

		var alerts = serviceResponse.getAlerts();
		var headers = new HttpHeaders();

		try {
			var response = authService.loginV2(request);

			var cookie = authService.setCookie(response.getToken());

			headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

			return getServiceResponse(true, response.getUserId(), HttpStatus.OK, headers, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			headers.add(HttpHeaders.SET_COOKIE, NO_TOKEN_COOKIE_HEADER);
		}

		return getServiceResponse(true, null, HttpStatus.UNAUTHORIZED, headers, alerts);
	}

	@PostMapping("/logout")
	public ResponseEntity<ServiceResponse<Object>> logout() {
		var alerts = serviceResponse.getAlerts();

		try {
			var headers = new HttpHeaders();

			headers.add(HttpHeaders.SET_COOKIE, NO_TOKEN_COOKIE_HEADER);

			return getServiceResponse(true, null, HttpStatus.OK, headers, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}

		return getServiceResponse(true, null, HttpStatus.INTERNAL_SERVER_ERROR, alerts);
	}

	@PostMapping("/switch-role/{contextId}/{role}")
	private ResponseEntity<ServiceResponse<UUID>> switchRole(@PathVariable UUID contextId, HttpServletResponse response,
			@PathVariable UserRole role) {
		var alerts = serviceResponse.getAlerts();
		try {
			authService.switchUserRole(contextId, response, role);
			return getServiceResponse(true, contextId, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}

	@GetMapping("/user-profiles/{userId}")
	private ResponseEntity<ServiceResponse<List<UserProfileDTO>>> getUserProfiles(@PathVariable UUID userId) {
		var alerts = serviceResponse.getAlerts();
		try {
			var profiles = authService.getUserProfiles(userId);
			return getServiceResponse(true, profiles, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.WARNING));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}

	@PostMapping("/verify-account/{userId}/method/{method}")
	private ResponseEntity<ServiceResponse<Boolean>> verifyAccount(@PathVariable UUID userId,
			@PathVariable AccountVerificationMethod method) {
		var alerts = serviceResponse.getAlerts();
		try {
			var isVerified = authService.verifyAccount(userId, method);
			return getServiceResponse(true, isVerified, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}
}
