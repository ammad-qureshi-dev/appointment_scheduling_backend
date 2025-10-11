/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.UserDTO;
import com.booker_app.backend_service.controllers.response.dto.UserProfileDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.enums.UserRole;
import com.booker_app.backend_service.services.AuthService;
import com.booker_app.backend_service.services.JwtService;
import com.booker_app.backend_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Auth.TOKEN;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(BASE_URL + "/v1/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final ServiceResponse<?> serviceResponse;
	private final AuthService authService;
	private final JwtService jwtService;

	@GetMapping("/me")
	public ResponseEntity<ServiceResponse<UserDTO>> getMe(@CookieValue("token") String token) {
		var alerts = serviceResponse.getAlerts();
		try {
			var userId = jwtService.extractUserId(token);
			var user = userService.getUserById(userId);
			return getServiceResponse(true, user, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}

	@PostMapping("/switch-profile/{contextId}")
	@Operation(description = "Post-login, allows user to select a profile and load those specific details")
	public ResponseEntity<ServiceResponse<UUID>> switchRole(@RequestParam UserRole activeProfile,
			@PathVariable UUID contextId, @CookieValue(TOKEN) String token) {
		var alerts = serviceResponse.getAlerts();
		var headers = new HttpHeaders();
		try {
			var userId = jwtService.extractUserId(token);
			var response = userService.switchUserRole(userId, contextId, activeProfile);
			var cookie = authService.generateCookie(response.getToken());
			headers.set(HttpHeaders.SET_COOKIE, cookie.toString());
			return getServiceResponse(true, response.getUserId(), HttpStatus.OK, headers, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}

	@GetMapping("/user-profiles")
	@Operation(description = "Post-login operation, retrieves all profiles user has, like multiple business or jobs")
	public ResponseEntity<ServiceResponse<List<UserProfileDTO>>> getUserProfiles(@CookieValue(TOKEN) String token) {
		var alerts = serviceResponse.getAlerts();
		try {
			var userId = jwtService.extractUserId(token);
			var profiles = userService.getUserProfiles(userId);
			return getServiceResponse(true, profiles, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.WARNING));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}
}
