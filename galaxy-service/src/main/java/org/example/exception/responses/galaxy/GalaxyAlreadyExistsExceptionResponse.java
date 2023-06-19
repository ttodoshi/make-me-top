package org.example.exception.responses.galaxy;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class GalaxyAlreadyExistsExceptionResponse extends ErrorResponse {
    public GalaxyAlreadyExistsExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
