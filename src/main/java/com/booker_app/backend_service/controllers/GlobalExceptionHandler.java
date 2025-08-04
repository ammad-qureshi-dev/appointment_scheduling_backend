package com.booker_app.backend_service.controllers;

import com.booker_app.backend_service.controllers.response.ServiceResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import static com.booker_app.backend_service.controllers.response.ServiceResponse.getServiceResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ServiceResponse<String>> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ServiceResponse<String>> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    private static ResponseEntity<ServiceResponse<String>> buildErrorResponse(Exception ex, HttpStatus httpStatus) {
        return getServiceResponse(false, ex.getMessage(), httpStatus);
    }

}
