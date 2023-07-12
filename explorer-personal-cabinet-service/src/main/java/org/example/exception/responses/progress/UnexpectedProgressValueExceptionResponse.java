package org.example.exception.responses.progress;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class UnexpectedProgressValueExceptionResponse extends ErrorResponse {
    public UnexpectedProgressValueExceptionResponse(Exception e) {
        super(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    }
}
