package org.example.courseregistration.controller;

import io.grpc.StatusRuntimeException;
import org.example.courseregistration.dto.error.ErrorResponseDto;
import org.example.courseregistration.exception.connect.ConnectException;
import org.example.courseregistration.exception.course.CourseNotFoundException;
import org.example.courseregistration.exception.courserequest.*;
import org.example.courseregistration.exception.keeper.DifferentKeeperException;
import org.example.courseregistration.exception.keeper.KeeperNotFoundException;
import org.example.courseregistration.exception.person.PersonNotFoundException;
import org.example.courseregistration.exception.progress.AlreadyStudyingException;
import org.example.courseregistration.exception.progress.PersonIsStudyingException;
import org.example.courseregistration.exception.progress.SystemParentsNotCompletedException;
import org.example.courseregistration.exception.progress.TeachingInProcessException;
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

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleStatusNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRequestNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCourseNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(KeeperNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleKeeperNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DifferentKeeperException.class)
    public ResponseEntity<ErrorResponseDto> handleDifferentKeeperException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NoApprovedRequestsFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoApprovedRequestsFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(AlreadyStudyingException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyStudyingException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(PersonIsStudyingException.class)
    public ResponseEntity<ErrorResponseDto> handlePersonIsStudyingException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(SystemParentsNotCompletedException.class)
    public ResponseEntity<ErrorResponseDto> handleSystemParentsNotCompletedException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(PersonIsKeeperException.class)
    public ResponseEntity<ErrorResponseDto> handlePersonIsKeeperException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(PersonIsNotPersonInRequestException.class)
    public ResponseEntity<ErrorResponseDto> handlePersonIsNotPersonInRequestException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(RequestAlreadySentException.class)
    public ResponseEntity<ErrorResponseDto> handleRequestAlreadySentException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(RequestAlreadyClosedException.class)
    public ResponseEntity<ErrorResponseDto> handleRequestAlreadyClosedException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TeachingInProcessException.class)
    public ResponseEntity<ErrorResponseDto> handleTeachingInProcessException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(RejectionReasonNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRejectionReasonNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
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
