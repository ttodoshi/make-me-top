package org.example.config;

import org.example.exception.classes.accessEX.AccessException;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.planetEX.PlanetAlreadyExistsException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.access.AccessExceptionResponse;
import org.example.exception.responses.connect.ConnectExceptionResponse;
import org.example.exception.responses.galaxy.GalaxyNotFoundExceptionResponse;
import org.example.exception.responses.planet.PlanetAlreadyExistsExceptionResponse;
import org.example.exception.responses.planet.PlanetNotFoundExceptionResponse;
import org.example.exception.responses.system.SystemNotFoundExceptionResponse;
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
    @ExceptionHandler(SystemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSystemNotFoundException(SystemNotFoundException e) {
        return new ResponseEntity<>(new SystemNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GalaxyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyNotFoundException(GalaxyNotFoundException e) {
        return new ResponseEntity<>(new GalaxyNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlanetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanetNotFoundException(PlanetNotFoundException e) {
        return new ResponseEntity<>(new PlanetNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlanetAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePlanetAlreadyExistsException(PlanetAlreadyExistsException e) {
        return new ResponseEntity<>(new PlanetAlreadyExistsExceptionResponse(e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponse> handleConnectException(ConnectException e) {
        return new ResponseEntity<>(new ConnectExceptionResponse(e), HttpStatus.BAD_GATEWAY);
    }
}
