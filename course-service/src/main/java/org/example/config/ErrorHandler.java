package org.example.config;

import org.example.exception.ErrorResponse;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), "Вам закрыт доступ к данной функциональности бортового компьютера"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFoundException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CourseThemeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseThemeNotFoundException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
