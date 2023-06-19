package org.example.exception.responses.user;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class UserNotFoundExceptionResponse extends ErrorResponse {
    public UserNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
