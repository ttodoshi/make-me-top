package org.example.exception.responses.connect;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class ConnectExceptionResponse extends ErrorResponse {

    public ConnectExceptionResponse(Exception e) {
        super(HttpStatus.BAD_GATEWAY, e.getMessage());
    }
}
