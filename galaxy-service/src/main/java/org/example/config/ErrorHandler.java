package org.example.config;

import org.example.exception.ErrorResponse;
import org.example.exception.classes.dependencyEX.DependencyAlreadyExistsException;
import org.example.exception.classes.dependencyEX.DependencyCouldNotBeCreatedException;
import org.example.exception.classes.dependencyEX.DependencyNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyAlreadyExistsException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.orbitEX.OrbitCoordinatesException;
import org.example.exception.classes.orbitEX.OrbitNotFoundException;
import org.example.exception.classes.systemEX.SystemAlreadyExistsException;
import org.example.exception.classes.systemEX.SystemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.logging.Logger;

@RestControllerAdvice
public class ErrorHandler {
    private final Logger logger = Logger.getLogger(ErrorHandler.class.getName());

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), "Вам закрыт доступ к данной функциональности бортового компьютера"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Ошибка в поступивших данных"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Ошибка в поступивших данных"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Ошибка в поступивших данных"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Ошибка в поступивших данных"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GalaxyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GalaxyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyAlreadyExistsException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(OrbitNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrbitNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrbitCoordinatesException.class)
    public ResponseEntity<ErrorResponse> handleOrbitCoordinatesException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SystemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSystemNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SystemAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSystemAlreadyExistsException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DependencyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDependencyNotFound(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DependencyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDependencyAlreadyExistsException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DependencyCouldNotBeCreatedException.class)
    public ResponseEntity<ErrorResponse> handleDependencyCouldNotBeCreatedException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }
}
