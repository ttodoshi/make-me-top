package org.example.exception.responses.request;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class RequestNotDeniedExceptionResponse extends ErrorResponse {
    public RequestNotDeniedExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
