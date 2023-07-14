package org.example.config;

import org.example.exception.ErrorResponse;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.keeperEX.DifferentKeeperException;
import org.example.exception.classes.markEX.ExplorerDoesNotNeedMarkException;
import org.example.exception.classes.markEX.UnexpectedMarkValueException;
import org.example.exception.classes.requestEX.*;
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

    @ExceptionHandler(DifferentKeeperException.class)
    public ResponseEntity<ErrorResponse> handleDifferentKeeperException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatusNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(KeeperRejectionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleKeeperRejectionAlreadyExistsException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RequestAlreadyClosedException.class)
    public ResponseEntity<ErrorResponse> handleRequestAlreadyClosedException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestNotDeniedException.class)
    public ResponseEntity<ErrorResponse> handleRequestNotDeniedException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExplorerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExplorerNotFoundException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnexpectedMarkValueException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedMarkValueException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExplorerDoesNotNeedMarkException.class)
    public ResponseEntity<ErrorResponse> handleExplorerDoesNotNeedMarkException(Exception e) {
        logger.warning(e.toString());
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
