package org.example.exception.responses.progress;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class ProgressDecreaseExceptionResponse extends ErrorResponse {
    public ProgressDecreaseExceptionResponse(Exception e) {
        super(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    }
}
