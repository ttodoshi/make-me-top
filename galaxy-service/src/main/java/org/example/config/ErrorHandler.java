package org.example.config;

import org.example.exception.galaxyEX.GalaxyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(GalaxyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GalaxyNotFoundException handleGalaxyNotFoundException(GalaxyNotFoundException e) {
        return e;
    }
}
