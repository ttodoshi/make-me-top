package org.example.exception.responses.keeper;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class KeeperNotFoundExceptionResponse extends ErrorResponse {
    public KeeperNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
