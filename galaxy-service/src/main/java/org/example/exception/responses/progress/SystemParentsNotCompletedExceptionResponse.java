package org.example.exception.responses.progress;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class SystemParentsNotCompletedExceptionResponse extends ErrorResponse {
    public SystemParentsNotCompletedExceptionResponse(Exception e) {
        super(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage());
    }
}
