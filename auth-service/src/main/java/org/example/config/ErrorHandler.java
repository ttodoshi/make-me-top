package org.example.config;

import org.example.exception.classes.user.RoleNotAvailableException;
import org.example.exception.classes.user.UserNotFoundException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.user.RoleNotAvailableExceptionResponse;
import org.example.exception.responses.user.UserNotFoundExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.ok(new UserNotFoundExceptionResponse(e));
    }

    @ExceptionHandler(RoleNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotAvailableException(RoleNotAvailableException e) {
        return ResponseEntity.ok(new RoleNotAvailableExceptionResponse(e));
    }
}
