package org.example.exception.responses.keeper;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class DifferentKeeperExceptionResponse extends ErrorResponse {
    public DifferentKeeperExceptionResponse(Exception e) {
        super(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
