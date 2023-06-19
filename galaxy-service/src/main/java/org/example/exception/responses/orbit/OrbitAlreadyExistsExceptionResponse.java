package org.example.exception.responses.orbit;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class OrbitAlreadyExistsExceptionResponse extends ErrorResponse {
    public OrbitAlreadyExistsExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
