/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.EmployeeRequest;
import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.EmployeeDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@RestController
@RequestMapping(BASE_URL_V1 + "/employee")
public class EmployeeController {

	private final EmployeeService employeeService;
	private final List<ResponseData> responseData = new ArrayList<>();

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@PostMapping("/{businessId}")
	public ResponseEntity<ServiceResponse<UUID>> addEmployee(@PathVariable UUID businessId,
			@RequestBody EmployeeRequest request) {
		try {
			var userId = employeeService.addEmployeeToBusiness(businessId, request);
			return getServiceResponse(true, userId, HttpStatus.CREATED);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@GetMapping("/{businessId}")
	public ResponseEntity<ServiceResponse<List<EmployeeDTO>>> getAllEmployees(@PathVariable UUID businessId) {
		try {
			var result = employeeService.getAllEmployees(businessId);
			return getServiceResponse(true, result, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@DeleteMapping("/{businessId}")
	public ResponseEntity<ServiceResponse<Boolean>> removeEmployee(@PathVariable UUID businessId,
			@RequestBody EmployeeRequest request) {
		try {
			var isDeleted = employeeService.removeEmployeeFromBusiness(businessId, request);
			return getServiceResponse(true, isDeleted, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

}
