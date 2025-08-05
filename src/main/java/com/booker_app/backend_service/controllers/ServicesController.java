/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.ServiceRequest;
import com.booker_app.backend_service.controllers.request.SimpleServiceRequest;
import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.ServiceDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.services.ServicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@RestController
@RequestMapping(BASE_URL_V1 + "/services")
public class ServicesController {

	private final List<ResponseData> responseData = new ArrayList<>();
	private final ServicesService servicesService;

	public ServicesController(ServicesService servicesService) {
		this.servicesService = servicesService;
	}

	@PostMapping("/{companyId}")
	public ResponseEntity<ServiceResponse<Boolean>> addService(@PathVariable UUID companyId,
			@RequestBody List<ServiceRequest> request) {
		try {
			servicesService.addService(companyId, request);
			return getServiceResponse(true, true, HttpStatus.CREATED);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@GetMapping("/{companyId}/search")
	public ResponseEntity<ServiceResponse<List<ServiceDTO>>> getAllServices(@PathVariable UUID companyId) {
		try {
			var services = servicesService.getAllServices(companyId);
			return getServiceResponse(true, services, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@DeleteMapping("/{companyId}")
	public ResponseEntity<ServiceResponse<Boolean>> removeService(@PathVariable UUID companyId,
			@RequestBody SimpleServiceRequest request) {
		try {
			var services = servicesService.removeService(companyId, request);
			return getServiceResponse(true, services, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@PutMapping("/{companyId}")
	public ResponseEntity<ServiceResponse<Boolean>> updateService(@PathVariable UUID companyId,
			@RequestBody ServiceRequest request) {
		try {
			var isUpdated = servicesService.updateService(companyId, request);
			return getServiceResponse(true, isUpdated, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}
}
