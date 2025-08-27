/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.booker_app.backend_service.controllers.request.AppointmentRequest;
import com.booker_app.backend_service.controllers.request.AppointmentStatusRequest;
import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.controllers.response.dto.AppointmentDTO;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.AppointmentStatus;
import com.booker_app.backend_service.services.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@RestController
@RequestMapping(BASE_URL_V1 + "/appointment")
public class AppointmentController {

	private final AppointmentService appointmentService;
	private final List<ResponseData> responseData = new ArrayList<>();

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@PostMapping("/business/{businessId}/customer/{customerId}")
	public ResponseEntity<ServiceResponse<UUID>> createAppointment(@PathVariable("businessId") UUID businessId,
			@PathVariable("customerId") UUID customerId, @RequestBody AppointmentRequest request) {
		try {
			var appointmentId = appointmentService.createAppointment(businessId, customerId, request);
			return getServiceResponse(true, appointmentId, HttpStatus.CREATED);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@GetMapping("/business/{businessId}/search/{appointmentDate}")
	public ResponseEntity<ServiceResponse<List<AppointmentDTO>>> getAppointmentsByAppointmentDate(
			@PathVariable("businessId") UUID businessId, @PathVariable("appointmentDate") LocalDate appointmentDate) {
		try {
			var appointmentId = appointmentService.getAppointmentsByAppointmentDate(businessId, appointmentDate);
			return getServiceResponse(true, appointmentId, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, Collections.emptyList(), HttpStatus.BAD_REQUEST, responseData);
	}

	@PutMapping("/business/{businessId}/update/{appointmentId}/customer/{customerId}")
	public ResponseEntity<ServiceResponse<Boolean>> updateAppointment(@PathVariable("businessId") UUID businessId,
			@PathVariable("appointmentId") UUID appointmentId, @PathVariable("customerId") UUID customerId,
			@RequestBody AppointmentRequest request) {
		try {
			var isUpdated = appointmentService.updateAppointment(businessId, appointmentId, customerId, request);
			return getServiceResponse(true, isUpdated, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, false, HttpStatus.BAD_REQUEST, responseData);
	}

	@PutMapping("/{appointmentId}/status")
	public ResponseEntity<ServiceResponse<AppointmentStatus>> updateAppointment(
			@PathVariable("appointmentId") UUID appointmentId, @RequestBody AppointmentStatusRequest request) {
		try {
			var status = appointmentService.updateAppointmentStatus(appointmentId, request);
			return getServiceResponse(true, status, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}

	@PostMapping("/{appointmentId}/assign-to/{employeeId}")
	public ResponseEntity<ServiceResponse<Boolean>> assignAppointmentToEmployee(
			@PathVariable("appointmentId") UUID appointmentId, @PathVariable("employeeId") UUID employeeId) {
		try {
			var isAssigned = appointmentService.assignAppointmentToEmployee(appointmentId, employeeId);
			return getServiceResponse(true, isAssigned, HttpStatus.OK);
		} catch (ServiceResponseException e) {
			responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}
		return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
	}
}
