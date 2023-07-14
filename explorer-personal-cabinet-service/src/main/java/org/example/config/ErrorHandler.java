package org.example.config;

import org.example.exception.ErrorResponse;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.exception.classes.progressEX.PlanetAlreadyCompletedException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.progressEX.UnexpectedCourseThemeException;
import org.example.exception.classes.progressEX.UnexpectedProgressValueException;
import org.example.exception.classes.requestEX.PersonIsKeeperException;
import org.example.exception.classes.requestEX.PersonIsStudyingException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
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

    @ExceptionHandler(UnexpectedProgressValueException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedProgressValueException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(PlanetAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handlePlanetAlreadyCompletedException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SystemParentsNotCompletedException.class)
    public ResponseEntity<ErrorResponse> handleSystemParentsNotCompletedException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), e.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponse> handleConnectException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KeeperNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleKeeperNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatusNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersonIsKeeperException.class)
    public ResponseEntity<ErrorResponse> handlePersonIsKeeperException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PersonIsStudyingException.class)
    public ResponseEntity<ErrorResponse> handlePersonIsStudyingException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExplorerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExplorerNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnexpectedCourseThemeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedCourseThemeException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CourseThemeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseThemeNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GalaxyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGalaxyNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
