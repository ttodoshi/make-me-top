package org.example.exception.responses.orbit;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class OrbitDeleteExceptionResponse extends ErrorResponse {
    public OrbitDeleteExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
