/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.exceptions;

import com.booker_app.backend_service.controllers.response.ResponseType;

public class BusinessNameTakenException extends RuntimeException {
	public BusinessNameTakenException(ResponseType message) {
		super(String.valueOf(message));
	}
}
