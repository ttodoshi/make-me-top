package org.example.config;

import org.example.exception.ErrorResponse;
import org.example.exception.classes.user.RoleNotAvailableException;
import org.example.exception.classes.user.UserNotFoundException;
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotAvailableException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
