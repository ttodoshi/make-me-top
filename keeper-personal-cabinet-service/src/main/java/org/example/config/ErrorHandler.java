package org.example.config;

import org.example.exception.classes.accessEX.AccessException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.access.AccessExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ErrorResponse> handleAccessException(AccessException e) {
        return new ResponseEntity<>(new AccessExceptionResponse(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(new AccessExceptionResponse(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
        return new ResponseEntity<>(new AccessExceptionResponse(), HttpStatus.FORBIDDEN);
    }
}
