package org.example.exception.responses.user;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class RoleNotAvailableExceptionResponse extends ErrorResponse {
    public RoleNotAvailableExceptionResponse(Exception e) {
        super(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
