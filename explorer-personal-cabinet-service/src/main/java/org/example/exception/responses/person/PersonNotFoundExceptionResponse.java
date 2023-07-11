package org.example.exception.responses.person;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PersonNotFoundExceptionResponse extends ErrorResponse {
    public PersonNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
