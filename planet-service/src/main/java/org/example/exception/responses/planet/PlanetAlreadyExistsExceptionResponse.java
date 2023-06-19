package org.example.exception.responses.planet;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PlanetAlreadyExistsExceptionResponse extends ErrorResponse {
    public PlanetAlreadyExistsExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
