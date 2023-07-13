package org.example.exception.responses.explorer;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class ExplorerNotFoundExceptionResponse extends ErrorResponse {
    public ExplorerNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
