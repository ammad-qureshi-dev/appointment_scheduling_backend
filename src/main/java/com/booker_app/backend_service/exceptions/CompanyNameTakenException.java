package com.booker_app.backend_service.exceptions;

import com.booker_app.backend_service.controllers.response.ResponseType;

public class CompanyNameTakenException extends RuntimeException{
    public CompanyNameTakenException (ResponseType message) {
        super(String.valueOf(message));
    }
}
