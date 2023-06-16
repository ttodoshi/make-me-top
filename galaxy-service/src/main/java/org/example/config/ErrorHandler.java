package org.example.config;

import org.example.exception.access.AccessException;
import org.example.exception.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.dependencyEX.DependencyNotFound;
import org.example.exception.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.galaxyEX.GalaxyNotFoundException;
import org.example.exception.orbitEX.OrbitAlreadyExistsException;
import org.example.exception.orbitEX.OrbitCoordinatesException;
import org.example.exception.orbitEX.OrbitDeleteException;
import org.example.exception.orbitEX.OrbitNotFoundException;
import org.example.exception.systemEX.SystemAlreadyExistsException;
import org.example.exception.systemEX.SystemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AccessException handleAccessException(AccessException e) {
        return e;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AccessException handleAccessDeniedException(AccessDeniedException e) {
        return new AccessException();
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AccessException handleSignatureException(SignatureException e) {
        return new AccessException();
    }

    @ExceptionHandler(GalaxyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GalaxyNotFoundException handleGalaxyNotFoundException(GalaxyNotFoundException e) {
        return e;
    }

    @ExceptionHandler(GalaxyAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public GalaxyAlreadyExistsException handleGalaxyAlreadyExistsException(GalaxyAlreadyExistsException e) {
        return e;
    }

    @ExceptionHandler(OrbitNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public OrbitNotFoundException handleOrbitNotFoundException(OrbitNotFoundException e) {
        return e;
    }

    @ExceptionHandler(OrbitAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public OrbitAlreadyExistsException handleOrbitAlreadyExistsException(OrbitAlreadyExistsException e) {
        return e;
    }

    @ExceptionHandler(OrbitDeleteException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public OrbitDeleteException handleOrbitDeleteException(OrbitDeleteException e) {
        return e;
    }

    @ExceptionHandler(OrbitCoordinatesException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public OrbitCoordinatesException handleOrbitCoordinatesException(OrbitCoordinatesException e) {
        return e;
    }

    @ExceptionHandler(SystemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SystemNotFoundException handleSystemNotFoundException(SystemNotFoundException e) {
        return e;
    }

    @ExceptionHandler(SystemAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public SystemAlreadyExistsException handleSystemAlreadyExistsException(SystemAlreadyExistsException e) {
        return e;
    }

    @ExceptionHandler(DependencyNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public DependencyNotFound handleDependencyNotFound(DependencyNotFound e) {
        return e;
    }

    @ExceptionHandler(DependencyAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public DependencyAlreadyExistsException handleDependencyAlreadyExistsException(DependencyAlreadyExistsException e) {
        return e;
    }
}
