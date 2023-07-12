package org.example.exception.responses.request;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PersonIsStudyingExceptionResponse extends ErrorResponse {
    public PersonIsStudyingExceptionResponse(Exception e) {
        super(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
