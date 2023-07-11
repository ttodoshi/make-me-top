package org.example.config;

import org.example.exception.classes.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.classes.dependencyEX.DependencyNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.access.AccessExceptionResponse;
import org.example.exception.responses.dependency.DependencyAlreadyExistsExceptionResponse;
import org.example.exception.responses.dependency.DependencyNotFoundExceptionResponse;
import org.example.exception.responses.galaxy.GalaxyAlreadyExistsExceptionResponse;
import org.example.exception.responses.galaxy.GalaxyNotFoundExceptionResponse;
import org.example.exception.responses.orbit.OrbitCoordinatesExceptionResponse;
import org.example.exception.responses.orbit.OrbitNotFoundExceptionResponse;
import org.example.exception.responses.system.SystemAlreadyExistsExceptionResponse;
import org.example.exception.responses.system.SystemNotFoundExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(new AccessExceptionResponse(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(GalaxyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyNotFoundException(GalaxyNotFoundException e) {
        return new ResponseEntity<>(new GalaxyNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GalaxyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyAlreadyExistsException(GalaxyAlreadyExistsException e) {
        return new ResponseEntity<>(new GalaxyAlreadyExistsExceptionResponse(e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(OrbitNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrbitNotFoundException(OrbitNotFoundException e) {
        return new ResponseEntity<>(new OrbitNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrbitCoordinatesException.class)
    public ResponseEntity<ErrorResponse> handleOrbitCoordinatesException(OrbitCoordinatesException e) {
        return new ResponseEntity<>(new OrbitCoordinatesExceptionResponse(e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SystemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSystemNotFoundException(SystemNotFoundException e) {
        return new ResponseEntity<>(new SystemNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SystemAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSystemAlreadyExistsException(SystemAlreadyExistsException e) {
        return new ResponseEntity<>(new SystemAlreadyExistsExceptionResponse(e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DependencyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDependencyNotFound(DependencyNotFoundException e) {
        return new ResponseEntity<>(new DependencyNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DependencyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDependencyAlreadyExistsException(DependencyAlreadyExistsException e) {
        return new ResponseEntity<>(new DependencyAlreadyExistsExceptionResponse(e), HttpStatus.FORBIDDEN);
    }
}
