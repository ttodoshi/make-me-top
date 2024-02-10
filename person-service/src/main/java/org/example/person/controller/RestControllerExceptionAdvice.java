package org.example.person.controller;

import org.example.person.dto.error.ErrorResponseDto;
import org.example.person.exception.connect.ConnectException;
import org.example.person.exception.course.CourseNotFoundException;
import org.example.person.exception.explorer.ExplorerGroupNotFoundException;
import org.example.person.exception.explorer.ExplorerNotFoundException;
import org.example.person.exception.explorer.PersonIsExplorerException;
import org.example.person.exception.keeper.KeeperAlreadyExistsException;
import org.example.person.exception.keeper.KeeperNotFoundException;
import org.example.person.exception.person.PersonNotFoundException;
import org.example.person.exception.planet.PlanetNotFoundException;
import org.example.person.exception.progress.ExplorerAlreadyHasMarkException;
import org.example.person.exception.request.PersonHaveOpenedCourseRegistrationRequestException;
import org.example.person.exception.request.RequestNotFoundException;
import org.example.person.exception.role.RoleNotAvailableException;
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

    @ExceptionHandler(RoleNotAvailableException.class)
    public ResponseEntity<ErrorResponseDto> handleRoleNotAvailableException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
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

    @ExceptionHandler(KeeperAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleKeeperAlreadyExistsException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(PersonIsExplorerException.class)
    public ResponseEntity<ErrorResponseDto> handlePersonIsExplorerException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(PersonHaveOpenedCourseRegistrationRequestException.class)
    public ResponseEntity<ErrorResponseDto> handlePersonHaveOpenedCourseRegistrationRequestException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ExplorerAlreadyHasMarkException.class)
    public ResponseEntity<ErrorResponseDto> handleExplorerAlreadyHasMarkException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }
}
