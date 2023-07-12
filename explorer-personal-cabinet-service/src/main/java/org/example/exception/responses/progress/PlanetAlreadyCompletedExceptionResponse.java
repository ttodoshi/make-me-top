package org.example.exception.responses.progress;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PlanetAlreadyCompletedExceptionResponse extends ErrorResponse {
    public PlanetAlreadyCompletedExceptionResponse(Exception e) {
        super(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
