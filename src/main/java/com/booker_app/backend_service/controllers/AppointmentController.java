package com.booker_app.backend_service.controllers;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("/company/{companyId}/customer/{customerId}")
    public ResponseEntity<ServiceResponse<UUID>> createAppointment(@PathVariable("companyId") UUID companyId, @PathVariable("customerId") UUID customerId, @RequestBody AppointmentRequest request) {
        try {
            var appointmentId = appointmentService.createAppointment(companyId, customerId, request);
            return getServiceResponse(true, appointmentId, HttpStatus.CREATED);
        } catch (ServiceResponseException e) {
            responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
        }
        return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
    }

    @GetMapping("/company/{companyId}/search/{appointmentDate}")
    public ResponseEntity<ServiceResponse<List<AppointmentDTO>>> getAppointmentsByAppointmentDate(@PathVariable("companyId") UUID companyId, @PathVariable("appointmentDate") LocalDate appointmentDate) {
        try {
            var appointmentId = appointmentService.getAppointmentsByAppointmentDate(companyId, appointmentDate);
            return getServiceResponse(true, appointmentId, HttpStatus.OK);
        } catch (ServiceResponseException e) {
            responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
        }
        return getServiceResponse(false, Collections.emptyList(), HttpStatus.BAD_REQUEST, responseData);
    }

    @PutMapping("/company/{companyId}/update/{appointmentId}/customer/{customerId}")
    public ResponseEntity<ServiceResponse<Boolean>> updateAppointment(@PathVariable("companyId") UUID companyId, @PathVariable("appointmentId") UUID appointmentId, @PathVariable("customerId") UUID customerId, @RequestBody AppointmentRequest request) {
        try {
            var isUpdated = appointmentService.updateAppointment(companyId, appointmentId, customerId, request);
            return getServiceResponse(true, isUpdated, HttpStatus.OK);
        } catch (ServiceResponseException e) {
            responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
        }
        return getServiceResponse(false, false, HttpStatus.BAD_REQUEST, responseData);
    }

    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<ServiceResponse<AppointmentStatus>> updateAppointment(@PathVariable("appointmentId") UUID appointmentId, @RequestBody AppointmentStatusRequest request) {
        try {
            var status = appointmentService.updateAppointmentStatus(appointmentId, request);
            return getServiceResponse(true, status, HttpStatus.OK);
        } catch (ServiceResponseException e) {
            responseData.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
        }
        return getServiceResponse(false, null, HttpStatus.BAD_REQUEST, responseData);
    }
}
