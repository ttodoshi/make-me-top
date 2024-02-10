package org.example.auth.controller;

import io.grpc.StatusRuntimeException;
import io.jsonwebtoken.MalformedJwtException;
import org.example.auth.dto.error.ErrorResponseDto;
import org.example.auth.exception.connect.ConnectException;
import org.example.auth.exception.person.PersonNotFoundException;
import org.example.auth.exception.person.RoleNotAvailableException;
import org.example.auth.exception.token.FailedLogoutException;
import org.example.auth.exception.token.FailedRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class RestControllerExceptionAdvice {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException() {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        "Вам закрыт доступ к данной функциональности бортового компьютера"
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequestExceptions() {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Ошибка в поступивших данных"
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponseDto> handleConnectException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponseDto> handleTimeoutException(Exception e) {
        return handleConnectException(new ConnectException(e));
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePersonNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RoleNotAvailableException.class)
    public ResponseEntity<ErrorResponseDto> handleRoleNotAvailableException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(FailedRefreshException.class)
    public ResponseEntity<ErrorResponseDto> handleFailedRefreshException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponseDto> handleMalformedJwtException(Exception e) {
        return handleFailedRefreshException(new FailedRefreshException(e));
    }

    @ExceptionHandler(FailedLogoutException.class)
    public ResponseEntity<ErrorResponseDto> handleFailedLogoutException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleStatusRuntimeException(StatusRuntimeException e) {
        HttpStatus httpStatus = HttpStatus.valueOf(e.getStatus().getCode().name());
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        httpStatus.getReasonPhrase(),
                        e.getStatus().getDescription()
                ),
                httpStatus
        );
    }
}
