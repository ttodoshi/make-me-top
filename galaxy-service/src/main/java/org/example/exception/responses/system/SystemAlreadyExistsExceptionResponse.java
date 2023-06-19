package org.example.exception.responses.system;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class SystemAlreadyExistsExceptionResponse extends ErrorResponse {
    public SystemAlreadyExistsExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
