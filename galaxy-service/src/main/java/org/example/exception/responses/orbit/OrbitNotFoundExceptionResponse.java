package org.example.exception.responses.orbit;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class OrbitNotFoundExceptionResponse extends ErrorResponse {
    public OrbitNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
