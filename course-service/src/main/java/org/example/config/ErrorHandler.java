package org.example.config;

import org.example.exception.ErrorResponse;
import org.example.exception.classes.courseEX.CourseAlreadyExistsException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeAlreadyExistsException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
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

    @ExceptionHandler(CourseThemeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCourseThemeAlreadyExistsException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CourseAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCourseAlreadyExistsException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CourseThemeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseThemeNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
