package org.example.exception.responses.system;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class SystemNotFoundExceptionResponse extends ErrorResponse {
    public SystemNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
