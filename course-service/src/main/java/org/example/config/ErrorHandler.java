package org.example.config;

import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.course.CourseNotFoundExceptionResponse;
import org.example.exception.responses.coursetheme.CourseThemeNotFoundExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFoundException(CourseNotFoundException e) {
        return new ResponseEntity<>(new CourseNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CourseThemeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseThemeNotFoundException(CourseThemeNotFoundException e) {
        return new ResponseEntity<>(new CourseThemeNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }
}
