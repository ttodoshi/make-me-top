package org.example.config;

import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.planetEX.PlanetAlreadyExistsException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.connect.ConnectExceptionResponse;
import org.example.exception.responses.galaxy.GalaxyNotFoundExceptionResponse;
import org.example.exception.responses.planet.PlanetAlreadyExistsExceptionResponse;
import org.example.exception.responses.planet.PlanetNotFoundExceptionResponse;
import org.example.exception.responses.system.SystemNotFoundExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(SystemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSystemNotFoundException(SystemNotFoundException e) {
        return ResponseEntity.ok(new SystemNotFoundExceptionResponse(e));
    }

    @ExceptionHandler(GalaxyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyNotFoundException(GalaxyNotFoundException e) {
        return ResponseEntity.ok(new GalaxyNotFoundExceptionResponse(e));
    }

    @ExceptionHandler(PlanetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanetNotFoundException(PlanetNotFoundException e) {
        return ResponseEntity.ok(new PlanetNotFoundExceptionResponse(e));
    }

    @ExceptionHandler(PlanetAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePlanetAlreadyExistsException(PlanetAlreadyExistsException e) {
        return ResponseEntity.ok(new PlanetAlreadyExistsExceptionResponse(e));
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponse> handleConnectException(ConnectException e) {
        return ResponseEntity.ok(new ConnectExceptionResponse(e));
    }
}
