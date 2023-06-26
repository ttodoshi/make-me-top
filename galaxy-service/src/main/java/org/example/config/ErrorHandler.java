package org.example.config;

import org.example.exception.classes.accessEX.AccessException;
import org.example.exception.classes.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.classes.dependencyEX.DependencyNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitAlreadyExistsException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitDeleteException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.progressEX.ProgressDecreaseException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.access.AccessExceptionResponse;
import org.example.exception.responses.dependency.DependencyAlreadyExistsExceptionResponse;
import org.example.exception.responses.dependency.DependencyNotFoundExceptionResponse;
import org.example.exception.responses.galaxy.GalaxyAlreadyExistsExceptionResponse;
import org.example.exception.responses.galaxy.GalaxyNotFoundExceptionResponse;
import org.example.exception.responses.orbit.OrbitAlreadyExistsExceptionResponse;
import org.example.exception.responses.orbit.OrbitCoordinatesExceptionResponse;
import org.example.exception.responses.orbit.OrbitDeleteExceptionResponse;
import org.example.exception.responses.orbit.OrbitNotFoundExceptionResponse;
import org.example.exception.responses.progress.ProgressDecreaseExceptionResponse;
import org.example.exception.responses.progress.SystemParentsNotCompletedExceptionResponse;
import org.example.exception.responses.system.SystemAlreadyExistsExceptionResponse;
import org.example.exception.responses.system.SystemNotFoundExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ErrorResponse> handleAccessException(AccessException e) {
        return ResponseEntity.ok(new AccessExceptionResponse(e));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.ok(new AccessExceptionResponse(e));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
        return ResponseEntity.ok(new AccessExceptionResponse(e));
    }

    @ExceptionHandler(ProgressDecreaseException.class)
    public ResponseEntity<ErrorResponse> handleProgressDecreaseException(ProgressDecreaseException e) {
        return ResponseEntity.ok(new ProgressDecreaseExceptionResponse(e));
    }

    @ExceptionHandler(SystemParentsNotCompletedException.class)
    public ResponseEntity<ErrorResponse> handleSystemParentsNotCompletedException(SystemParentsNotCompletedException e) {
        return ResponseEntity.ok(new SystemParentsNotCompletedExceptionResponse(e));
    }

    @ExceptionHandler(GalaxyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyNotFoundException(GalaxyNotFoundException e) {
        return ResponseEntity.ok(new GalaxyNotFoundExceptionResponse(e));
    }

    @ExceptionHandler(GalaxyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyAlreadyExistsException(GalaxyAlreadyExistsException e) {
        return ResponseEntity.ok(new GalaxyAlreadyExistsExceptionResponse(e));
    }

    @ExceptionHandler(OrbitNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrbitNotFoundException(OrbitNotFoundException e) {
        return ResponseEntity.ok(new OrbitNotFoundExceptionResponse(e));
    }

    @ExceptionHandler(OrbitAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleOrbitAlreadyExistsException(OrbitAlreadyExistsException e) {
        return ResponseEntity.ok(new OrbitAlreadyExistsExceptionResponse(e));
    }

    @ExceptionHandler(OrbitDeleteException.class)
    public ResponseEntity<ErrorResponse> handleOrbitDeleteException(OrbitDeleteException e) {
        return ResponseEntity.ok(new OrbitDeleteExceptionResponse(e));
    }

    @ExceptionHandler(OrbitCoordinatesException.class)
    public ResponseEntity<ErrorResponse> handleOrbitCoordinatesException(OrbitCoordinatesException e) {
        return ResponseEntity.ok(new OrbitCoordinatesExceptionResponse(e));
    }

    @ExceptionHandler(SystemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSystemNotFoundException(SystemNotFoundException e) {
        return ResponseEntity.ok(new SystemNotFoundExceptionResponse(e));
    }

    @ExceptionHandler(SystemAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSystemAlreadyExistsException(SystemAlreadyExistsException e) {
        return ResponseEntity.ok(new SystemAlreadyExistsExceptionResponse(e));
    }

    @ExceptionHandler(DependencyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDependencyNotFound(DependencyNotFoundException e) {
        return ResponseEntity.ok(new DependencyNotFoundExceptionResponse(e));
    }

    @ExceptionHandler(DependencyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDependencyAlreadyExistsException(DependencyAlreadyExistsException e) {
        return ResponseEntity.ok(new DependencyAlreadyExistsExceptionResponse(e));
    }
}
