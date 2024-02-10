package org.example.homework.controller;

import io.grpc.StatusRuntimeException;
import org.example.homework.dto.error.ErrorResponseDto;
import org.example.homework.exception.connect.ConnectException;
import org.example.homework.exception.explorer.ExplorerGroupIsNotOnCourseException;
import org.example.homework.exception.explorer.ExplorerGroupNotFoundException;
import org.example.homework.exception.explorer.ExplorerNotFoundException;
import org.example.homework.exception.explorer.ExplorerNotInGroupException;
import org.example.homework.exception.homework.*;
import org.example.homework.exception.keeper.DifferentKeeperException;
import org.example.homework.exception.keeper.KeeperNotForGroupException;
import org.example.homework.exception.keeper.KeeperNotFoundException;
import org.example.homework.exception.person.PersonNotFoundException;
import org.example.homework.exception.planet.PlanetNotFoundException;
import org.example.homework.exception.progress.UnexpectedCourseThemeException;
import org.example.homework.exception.request.StatusNotFoundException;
import org.example.homework.exception.theme.CourseThemeNotFoundException;
import org.example.homework.exception.theme.ThemeClosedException;
import org.example.homework.exception.theme.ThemeFromDifferentCourseException;
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

    @ExceptionHandler(ExplorerNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleExplorerNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HomeworkAlreadyCheckingException.class)
    public ResponseEntity<ErrorResponseDto> handleHomeworkAlreadyCheckingException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HomeworkRequestAlreadyClosedException.class)
    public ResponseEntity<ErrorResponseDto> handleHomeworkRequestAlreadyClosedException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(CourseThemeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCourseThemeNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UnexpectedCourseThemeException.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpectedCourseThemeException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
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

    @ExceptionHandler(HomeworkRequestNotFound.class)
    public ResponseEntity<ErrorResponseDto> handleHomeworkRequestNotFound(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HomeworkIsStillEditingException.class)
    public ResponseEntity<ErrorResponseDto> handleHomeworkIsStillEditingException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ExplorerGroupNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleExplorerGroupNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ExplorerGroupIsNotOnCourseException.class)
    public ResponseEntity<ErrorResponseDto> handleExplorerGroupIsNotOnCourseException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ExplorerNotInGroupException.class)
    public ResponseEntity<ErrorResponseDto> handleExplorerNotInGroupException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(KeeperNotForGroupException.class)
    public ResponseEntity<ErrorResponseDto> handleKeeperNotForGroupException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(HomeworkNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleHomeworkNotFoundException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ThemeClosedException.class)
    public ResponseEntity<ErrorResponseDto> handleThemeClosedException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ThemeFromDifferentCourseException.class)
    public ResponseEntity<ErrorResponseDto> handleThemeFromDifferentCourseException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(PlanetNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePlanetNotFoundException(Exception e) {
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
