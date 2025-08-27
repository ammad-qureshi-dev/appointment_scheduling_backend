/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.CustomerRequest;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.CustomerDTO;
import com.booker_app.backend_service.controllers.response.dto.CustomerOverviewDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.services.CustomerService;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@RestController
@RequestMapping(BASE_URL_V1 + "/customer")
public class CustomerController {
	private final CustomerService customerService;

	private final ServiceResponse<?> serviceResponse;

	public CustomerController(CustomerService customerService, ServiceResponse<?> serviceResponse) {
		this.customerService = customerService;
		this.serviceResponse = serviceResponse;
	}

	@PostMapping("/{businessId}")
	public ResponseEntity<ServiceResponse<UUID>> addCustomer(@PathVariable("businessId") UUID businessId,
			@RequestBody CustomerRequest request) {
		var alerts = serviceResponse.getAlerts();
		try {
			var customerId = customerService.addCustomer(businessId, request);
			return getServiceResponse(true, customerId, HttpStatus.CREATED, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.WARNING));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@GetMapping("/{businessId}/search/all")
	public ResponseEntity<ServiceResponse<List<CustomerDTO>>> findAllCustomers(
			@PathVariable("businessId") UUID businessId) {
		var alerts = serviceResponse.getAlerts();
		try {
			return getServiceResponse(true, customerService.getAllCustomers(businessId), HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@GetMapping("/{businessId}/search/single")
	public ResponseEntity<ServiceResponse<CustomerDTO>> findCustomerBySearch(
			@PathVariable("businessId") UUID businessId, @RequestBody CustomerRequest request) {
		var alerts = serviceResponse.getAlerts();
		try {
			var response = customerService.getCustomerByPhoneOrEmail(businessId, request);
			return getServiceResponse(true, response, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@DeleteMapping("/{businessId}")
	public ResponseEntity<ServiceResponse<Boolean>> deleteCustomer(@PathVariable("businessId") UUID businessId,
			@RequestBody CustomerRequest request) {
		var alerts = serviceResponse.getAlerts();
		try {
			var isDeleted = customerService.deleteCustomer(businessId, request);
			return getServiceResponse(true, isDeleted, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@GetMapping("/{customerId}/business/{businessId}/appointment-overview")
	@Description("Retrieves a customer's appointment status for the business like upcoming appointment, past appointments + services, and typical spend")
	public ResponseEntity<ServiceResponse<CustomerOverviewDTO>> getCustomerAppointmentOverview(
			@PathVariable("customerId") UUID customerId, @PathVariable("businessId") UUID businessId) {
		var alerts = serviceResponse.getAlerts();
		try {
			var overview = customerService.getCustomerAppointmentOverview(customerId, businessId);
			return getServiceResponse(true, overview, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@GetMapping("/{businessId}/search")
	public ResponseEntity<ServiceResponse<List<CustomerDTO>>> getCustomerBySearch(
			@PathVariable("businessId") UUID businessId, @RequestParam("param") String param) {
		var alerts = serviceResponse.getAlerts();
		try {
			var results = customerService.getCustomerBySearch(businessId, param);
			return getServiceResponse(true, results, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}
}
