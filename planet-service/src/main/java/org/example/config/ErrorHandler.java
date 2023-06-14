package org.example.config;

import org.example.exception.SystemNotFoundException;
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
}
