/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.exceptions;

public class AuthenticationException extends RuntimeException {
	public AuthenticationException(String message) {
		super(message);
	}
}
