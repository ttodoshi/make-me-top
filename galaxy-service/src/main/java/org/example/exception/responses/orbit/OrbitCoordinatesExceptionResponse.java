package org.example.exception.responses.orbit;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class OrbitCoordinatesExceptionResponse extends ErrorResponse {
    public OrbitCoordinatesExceptionResponse(Exception e) {
        super(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
