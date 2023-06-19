package org.example.exception.responses.planet;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PlanetNotFoundExceptionResponse extends ErrorResponse {
    public PlanetNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
