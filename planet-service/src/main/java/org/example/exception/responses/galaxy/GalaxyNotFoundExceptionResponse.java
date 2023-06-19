package org.example.exception.responses.galaxy;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class GalaxyNotFoundExceptionResponse extends ErrorResponse {
    public GalaxyNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
