/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.UserDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(BASE_URL_V1 + "/user")
public class UserController {

	private final UserService userService;
	private final ServiceResponse<?> serviceResponse;

	public UserController(UserService userService, ServiceResponse<?> serviceResponse) {
		this.userService = userService;
        this.serviceResponse = serviceResponse;
    }

	@GetMapping("/{userId}")
	public ResponseEntity<ServiceResponse<UserDTO>> getUserById(@PathVariable UUID userId) {
		var alerts = serviceResponse.getAlerts();
		try {
			var user = userService.getUserById(userId);
			return getServiceResponse(true, user, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
			return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
		}
	}
}
