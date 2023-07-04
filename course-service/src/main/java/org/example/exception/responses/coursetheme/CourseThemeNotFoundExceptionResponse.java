package org.example.exception.responses.coursetheme;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class CourseThemeNotFoundExceptionResponse extends ErrorResponse {
    public CourseThemeNotFoundExceptionResponse(Exception e) {
        super(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
