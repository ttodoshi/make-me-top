package org.example.exception.responses.dependency;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class DependencyAlreadyExistsExceptionResponse extends ErrorResponse {
    public DependencyAlreadyExistsExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
