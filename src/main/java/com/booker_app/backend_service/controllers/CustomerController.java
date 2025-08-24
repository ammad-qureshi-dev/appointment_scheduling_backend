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

	@PostMapping("/{companyId}")
	public ResponseEntity<ServiceResponse<UUID>> addCustomer(@PathVariable("companyId") UUID companyId,
			@RequestBody CustomerRequest request) {
		var alerts = serviceResponse.getAlerts();
		try {
			var customerId = customerService.addCustomer(companyId, request);
			return getServiceResponse(true, customerId, HttpStatus.CREATED, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.WARNING));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@GetMapping("/{companyId}/search/all")
	public ResponseEntity<ServiceResponse<List<CustomerDTO>>> findAllCustomers(
			@PathVariable("companyId") UUID companyId) {
		var alerts = serviceResponse.getAlerts();
		try {
			return getServiceResponse(true, customerService.getAllCustomers(companyId), HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@GetMapping("/{companyId}/search/single")
	public ResponseEntity<ServiceResponse<CustomerDTO>> findCustomerBySearch(@PathVariable("companyId") UUID companyId,
			@RequestBody CustomerRequest request) {
		var alerts = serviceResponse.getAlerts();
		try {
			var response = customerService.getCustomerByPhoneOrEmail(companyId, request);
			return getServiceResponse(true, response, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@DeleteMapping("/{companyId}")
	public ResponseEntity<ServiceResponse<Boolean>> deleteCustomer(@PathVariable("companyId") UUID companyId,
			@RequestBody CustomerRequest request) {
		var alerts = serviceResponse.getAlerts();
		try {
			var isDeleted = customerService.deleteCustomer(companyId, request);
			return getServiceResponse(true, isDeleted, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@GetMapping("/{customerId}/company/{companyId}/appointment-overview")
	@Description("Retrieves a customer's appointment status for the company like upcoming appointment, past appointments + services, and typical spend")
	public ResponseEntity<ServiceResponse<CustomerOverviewDTO>> getCustomerAppointmentOverview(
			@PathVariable("customerId") UUID customerId, @PathVariable("companyId") UUID companyId) {
		var alerts = serviceResponse.getAlerts();
		try {
			var overview = customerService.getCustomerAppointmentOverview(customerId, companyId);
			return getServiceResponse(true, overview, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}

	@GetMapping("/{companyId}/search")
	public ResponseEntity<ServiceResponse<List<CustomerDTO>>> getCustomerBySearch(
			@PathVariable("companyId") UUID companyId, @RequestParam("param") String param) {
		var alerts = serviceResponse.getAlerts();
		try {
			var results = customerService.getCustomerBySearch(companyId, param);
			return getServiceResponse(true, results, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, alerts);
	}
}
