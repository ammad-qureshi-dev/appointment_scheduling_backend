/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.UUID;

import com.booker_app.backend_service.controllers.request.LoginRequest;
import com.booker_app.backend_service.controllers.request.RegistrationRequest;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.enums.ContactMethod;
import com.booker_app.backend_service.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Auth.NO_TOKEN_COOKIE_HEADER;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/v1/auth")
@Tag(name = "Auth Controller", description = "APIs for authorization and authentication")
public class AuthController {

	private final AuthService authService;
	private final ServiceResponse<?> serviceResponse;

	public AuthController(AuthService authService, ServiceResponse<?> serviceResponse) {
		this.authService = authService;
		this.serviceResponse = serviceResponse;
	}

	@PostMapping("/register")
	@Operation(description = "Registers a new user")
	public ResponseEntity<ServiceResponse<UUID>> register(@RequestBody RegistrationRequest request) {
		var alerts = serviceResponse.getAlerts();
		var headers = new HttpHeaders();

		try {
			var response = authService.registerV2(request);
			var cookie = authService.generateCookie(response.getToken());
			headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
			return getServiceResponse(true, response.getUserId(), HttpStatus.OK, headers, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			headers.add(HttpHeaders.SET_COOKIE, NO_TOKEN_COOKIE_HEADER);
		}

		return getServiceResponse(true, null, HttpStatus.INTERNAL_SERVER_ERROR, headers, alerts);
	}

	@PostMapping("/login")
	@Operation(description = "User login")
	public ResponseEntity<ServiceResponse<UUID>> login(@RequestBody LoginRequest request) {
		var alerts = serviceResponse.getAlerts();
		var headers = new HttpHeaders();

		try {
			var response = authService.loginV2(request);
			var cookie = authService.generateCookie(response.getToken());
			headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
			return getServiceResponse(true, response.getUserId(), HttpStatus.OK, headers, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			headers.add(HttpHeaders.SET_COOKIE, NO_TOKEN_COOKIE_HEADER);
		}

		return getServiceResponse(true, null, HttpStatus.UNAUTHORIZED, headers, alerts);
	}

	@PostMapping("/logout")
	@Operation(description = "User logout")
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

	@PostMapping("/verify-account/{userId}")
	@Operation(description = "Post-registration operation, verifies user's account based on the authentication method")
	public ResponseEntity<ServiceResponse<Boolean>> verifyAccount(@PathVariable UUID userId,
			@RequestParam ContactMethod method) {
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
