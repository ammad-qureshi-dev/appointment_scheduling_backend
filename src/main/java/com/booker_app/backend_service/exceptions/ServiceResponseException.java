/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.exceptions;

import com.booker_app.backend_service.controllers.response.ResponseType;

public class ServiceResponseException extends RuntimeException {
	public ServiceResponseException(ResponseType message) {
		super(String.valueOf(message));
	}

	public ServiceResponseException(String message) {
		super(message);
	}
}
