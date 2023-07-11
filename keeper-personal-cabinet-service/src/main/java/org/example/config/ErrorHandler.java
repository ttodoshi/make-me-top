package org.example.config;

import org.example.exception.classes.accessEX.AccessException;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.requestEX.*;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.access.AccessExceptionResponse;
import org.example.exception.responses.keeper.DifferentKeeperExceptionResponse;
import org.example.exception.responses.request.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.util.NoSuchElementException;

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

    @ExceptionHandler(DifferentKeeperException.class)
    public ResponseEntity<ErrorResponse> handleDifferentKeeperException(DifferentKeeperException e) {
        return new ResponseEntity<>(new DifferentKeeperExceptionResponse(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotFoundException(RequestNotFoundException e) {
        return new ResponseEntity<>(new RequestNotFoundExceptionResponse(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatusNotFoundException(StatusNotFoundException e) {
        return new ResponseEntity<>(new StatusNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        return new ResponseEntity<>(new RequestNotFoundExceptionResponse(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KeeperRejectionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleKeeperRejectionAlreadyExistsException(KeeperRejectionAlreadyExistsException e) {
        return new ResponseEntity<>(new KeeperRejectionAlreadyExistsExceptionResponse(e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RequestAlreadyClosedException.class)
    public ResponseEntity<ErrorResponse> handleRequestAlreadyClosedException(RequestAlreadyClosedException e) {
        return new ResponseEntity<>(new RequestAlreadyClosedExceptionResponse(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestNotDeniedException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotDeniedException(RequestNotDeniedException e) {
        return new ResponseEntity<>(new RequestNotDeniedExceptionResponse(e), HttpStatus.FORBIDDEN);
    }
}
