package org.example.exception.responses.request;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class KeeperRejectionAlreadyExistsExceptionResponse extends ErrorResponse {
    public KeeperRejectionAlreadyExistsExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
