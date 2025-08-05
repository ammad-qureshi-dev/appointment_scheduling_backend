/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.CustomerRequest;
import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.CustomerDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@RestController
@RequestMapping(BASE_URL_V1 + "/customer")
public class CustomerController {
	private final List<ResponseData> responseData = new ArrayList<>();
	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping("/{companyId}")
	public ResponseEntity<ServiceResponse<UUID>> addCustomer(@PathVariable("companyId") UUID companyId,
			@RequestBody CustomerRequest request) {
		try {
			var customerId = customerService.addCustomer(companyId, request);
			return getServiceResponse(true, customerId, HttpStatus.CREATED);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@GetMapping("/{companyId}/search")
	public ResponseEntity<ServiceResponse<List<CustomerDTO>>> findAllCustomers(
			@PathVariable("companyId") UUID companyId) {
		try {
			return getServiceResponse(true, customerService.getAllCustomers(companyId), HttpStatus.CREATED);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@DeleteMapping("/{companyId}")
	public ResponseEntity<ServiceResponse<Boolean>> deleteCustomer(@PathVariable("companyId") UUID companyId,
			@RequestBody CustomerRequest request) {
		try {
			var isDeleted = customerService.deleteCustomer(companyId, request);
			return getServiceResponse(true, isDeleted, HttpStatus.CREATED);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}
}
