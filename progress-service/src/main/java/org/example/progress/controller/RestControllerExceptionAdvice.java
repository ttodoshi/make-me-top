package org.example.progress.controller;

import io.grpc.StatusRuntimeException;
import org.example.progress.dto.error.ErrorResponseDto;
import org.example.progress.exception.connect.ConnectException;
import org.example.progress.exception.course.CourseNotFoundException;
import org.example.progress.exception.explorer.ExplorerNotFoundException;
import org.example.progress.exception.galaxy.GalaxyNotFoundException;
import org.example.progress.exception.keeper.DifferentKeeperException;
import org.example.progress.exception.keeper.KeeperNotFoundException;
import org.example.progress.exception.mark.CourseMarkNotFoundException;
import org.example.progress.exception.mark.ExplorerDoesNotNeedMarkException;
import org.example.progress.exception.person.PersonNotFoundException;
import org.example.progress.exception.planet.PlanetNotFoundException;
import org.example.progress.exception.progress.HomeworkNotCompletedException;
import org.example.progress.exception.progress.ThemeAlreadyCompletedException;
import org.example.progress.exception.progress.UnexpectedCourseThemeException;
import org.example.progress.exception.system.SystemNotFoundException;
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

    @ExceptionHandler(SystemNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleSystemNotFoundException(Exception e) {
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

    @ExceptionHandler(ExplorerDoesNotNeedMarkException.class)
    public ResponseEntity<ErrorResponseDto> handleExplorerDoesNotNeedMarkException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ThemeAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponseDto> handlePlanetAlreadyCompletedException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
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

    @ExceptionHandler(HomeworkNotCompletedException.class)
    public ResponseEntity<ErrorResponseDto> handleHomeworkNotCompletedException(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(CourseMarkNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCourseMarkNotFoundException(Exception e) {
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

    @ExceptionHandler(GalaxyNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleGalaxyNotFoundException(Exception e) {
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
