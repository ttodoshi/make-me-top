package org.example.exception.responses.request;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class StatusNotFoundExceptionResponse extends ErrorResponse {
    public StatusNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
