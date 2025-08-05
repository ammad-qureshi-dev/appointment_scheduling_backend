/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.UserRegistrationRequest;
import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.exceptions.CompanyNameTakenException;
import com.booker_app.backend_service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@RestController
@RequestMapping(BASE_URL_V1 + "/user")
public class UserController {

	private final UserService userService;
	private final List<ResponseData> responseData = new ArrayList<>();

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<ServiceResponse<UUID>> registerUser(@RequestBody UserRegistrationRequest request) {
		try {
			var userId = userService.registerUser(request);
			return getServiceResponse(true, userId, HttpStatus.CREATED);
		} catch (CompanyNameTakenException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}

		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}
}
