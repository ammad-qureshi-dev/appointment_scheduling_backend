/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import com.booker_app.backend_service.controllers.request.CommsRequest;
import com.booker_app.backend_service.controllers.response.CommsResponse;
import com.booker_app.backend_service.controllers.response.ResponseSeverity;
import com.booker_app.backend_service.controllers.response.ServiceResponse;
import com.booker_app.backend_service.exceptions.ServiceResponseException;
import com.booker_app.backend_service.models.enums.ContactMethod;
import com.booker_app.backend_service.services.CommunicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;
import static com.booker_app.backend_service.utils.CommonUtils.generateResponseData;
import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/v1/comms")
public class CommunicationController {

	private final CommunicationService communicationService;
	private final ServiceResponse<?> serviceResponse;

	public CommunicationController(CommunicationService communicationService, ServiceResponse<?> serviceResponse) {
		this.communicationService = communicationService;
		this.serviceResponse = serviceResponse;
	}

	@PostMapping("/send")
	public ResponseEntity<ServiceResponse<CommsResponse>> sendCommunication(
			@RequestParam("commsType") ContactMethod commsType, @RequestBody CommsRequest<?> commsRequest) {

		var alerts = serviceResponse.getAlerts();
		CommsResponse response = null;

		try {
			response = communicationService.sendCommunication(commsType, commsRequest);
			return getServiceResponse(true, response, HttpStatus.OK, alerts);
		} catch (ServiceResponseException e) {
			alerts.add(generateResponseData(e.getMessage(), ResponseSeverity.ERROR));
		}

		return getServiceResponse(true, response, HttpStatus.OK, alerts);
	}
}
