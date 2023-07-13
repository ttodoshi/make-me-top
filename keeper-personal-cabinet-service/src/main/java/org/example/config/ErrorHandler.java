package org.example.config;

import org.example.exception.ErrorResponse;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.requestEX.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), "Вам закрыт доступ к данной функциональности бортового компьютера"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DifferentKeeperException.class)
    public ResponseEntity<ErrorResponse> handleDifferentKeeperException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotFoundException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatusNotFoundException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), "Бортовой компьютер не смог найти информацию о данном запросе"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KeeperRejectionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleKeeperRejectionAlreadyExistsException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RequestAlreadyClosedException.class)
    public ResponseEntity<ErrorResponse> handleRequestAlreadyClosedException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestNotDeniedException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotDeniedException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }
}
