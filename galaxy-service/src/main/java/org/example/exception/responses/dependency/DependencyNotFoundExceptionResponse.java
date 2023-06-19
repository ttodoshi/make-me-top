package org.example.exception.responses.dependency;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class DependencyNotFoundExceptionResponse extends ErrorResponse {
    public DependencyNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
