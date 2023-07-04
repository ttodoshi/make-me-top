package org.example.exception.responses.course;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class CourseNotFoundExceptionResponse extends ErrorResponse {
    public CourseNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
