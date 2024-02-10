package org.example.picture.controller;

import io.grpc.StatusRuntimeException;
import org.example.picture.dto.ErrorResponseDto;
import org.example.picture.exception.connect.ConnectException;
import org.example.picture.exception.person.PersonNotFoundException;
import org.example.picture.exception.picture.FileNotChangedException;
import org.example.picture.exception.picture.PictureNotFoundException;
import org.example.picture.exception.picture.PictureNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, ConstraintViolationException.class, MissingServletRequestParameterException.class, MaxUploadSizeExceededException.class})
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

    @ExceptionHandler(PictureNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handlePictureNotValidException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(PictureNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePictureNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(FileNotChangedException.class)
    public ResponseEntity<ErrorResponseDto> handleFileNotChangedException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
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
