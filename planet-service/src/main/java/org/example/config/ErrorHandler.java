package org.example.config;

import org.example.exception.connectException.ConnectException;
import org.example.exception.systemEX.SystemNotFoundException;
import org.example.exception.planetException.PlanetAlreadyExists;
import org.example.exception.planetException.PlanetNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(SystemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SystemNotFoundException handleSystemNotFoundException(SystemNotFoundException e) {
        return e;
    }

    @ExceptionHandler(PlanetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public PlanetNotFoundException handlePlanetNotFoundException(PlanetNotFoundException e) {
        return e;
    }

    @ExceptionHandler(PlanetAlreadyExists.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public PlanetAlreadyExists handlePlanetAlreadyExists(PlanetAlreadyExists e) {
        return e;
    }

    @ExceptionHandler(ConnectException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ConnectException handleConnectException(ConnectException e) {
        return e;
    }
}
