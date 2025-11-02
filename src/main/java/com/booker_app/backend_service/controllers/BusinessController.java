/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.NewBusinessRequest;
import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.BusinessDTO;
import com.booker_app.backend_service.exceptions.BusinessNameTakenException;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.services.BusinessService;
import com.booker_app.backend_service.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/v1/business")
public class BusinessController {

	private final BusinessService businessService;
	private final JwtService jwtService;
	private final List<ResponseData> responseData = new ArrayList<>();

	public BusinessController(BusinessService businessService, JwtService jwtService) {
		this.businessService = businessService;
		this.jwtService = jwtService;
	}

	@GetMapping("/{businessId}")
	public ResponseEntity<ServiceResponse<BusinessDTO>> getBusinessById(@PathVariable("businessId") UUID businessID) {
		try {
			var business = businessService.findBusinessById(businessID);
			return getServiceResponse(true, business, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@PostMapping("/register")
	public ResponseEntity<ServiceResponse<UUID>> createNewBusiness(@CookieValue("token") String token,
			@RequestBody NewBusinessRequest request) {
		try {
			var userId = jwtService.extractUserId(token);
			var businessId = businessService.createNewBusiness(request, userId);
			return getServiceResponse(true, businessId, HttpStatus.OK);

		} catch (BusinessNameTakenException | ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}

		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}
}
