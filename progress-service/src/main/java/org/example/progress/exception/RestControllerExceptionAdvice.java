package org.example.progress.exception;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.example.progress.exception.classes.connect.ConnectException;
import org.example.progress.exception.classes.course.CourseNotFoundException;
import org.example.progress.exception.classes.explorer.ExplorerNotFoundException;
import org.example.progress.exception.classes.keeper.DifferentKeeperException;
import org.example.progress.exception.classes.mark.CourseMarkNotFoundException;
import org.example.progress.exception.classes.mark.ExplorerDoesNotNeedMarkException;
import org.example.progress.exception.classes.person.PersonNotFoundException;
import org.example.progress.exception.classes.planet.PlanetNotFoundException;
import org.example.progress.exception.classes.progress.HomeworkNotCompletedException;
import org.example.progress.exception.classes.progress.ThemeAlreadyCompletedException;
import org.example.progress.exception.classes.progress.UnexpectedCourseThemeException;
import org.example.progress.exception.classes.system.SystemNotFoundException;
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
@Slf4j
public class RestControllerExceptionAdvice {
    private void logWarning(Throwable e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement firstStackTraceElement = stackTrace[0];
            String className = firstStackTraceElement.getClassName();
            String methodName = firstStackTraceElement.getMethodName();
            int lineNumber = firstStackTraceElement.getLineNumber();
            log.warn("Произошла ошибка в классе: {}, методе: {}, строка: {}\n\n" + e + "\n", className, methodName, lineNumber);
        } else log.warn(e.toString());
    }

    private void logError(Throwable e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement firstStackTraceElement = stackTrace[0];
            String className = firstStackTraceElement.getClassName();
            String methodName = firstStackTraceElement.getMethodName();
            int lineNumber = firstStackTraceElement.getLineNumber();
            log.error("Произошла ошибка в классе: {}, методе: {}, строка: {}\n\n" + e + "\n", className, methodName, lineNumber);
        } else log.error(e.toString());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), "Вам закрыт доступ к данной функциональности бортового компьютера"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Ошибка в поступивших данных"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Ошибка в поступивших данных"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Ошибка в поступивших данных"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Ошибка в поступивших данных"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonNotFoundException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponse> handleConnectException(Exception e) {
        logError(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(Exception e) {
        logError(e);
        return handleConnectException(new ConnectException());
    }

    @ExceptionHandler(ExplorerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExplorerNotFoundException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFoundException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SystemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSystemNotFoundException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DifferentKeeperException.class)
    public ResponseEntity<ErrorResponse> handleDifferentKeeperException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExplorerDoesNotNeedMarkException.class)
    public ResponseEntity<ErrorResponse> handleExplorerDoesNotNeedMarkException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ThemeAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handlePlanetAlreadyCompletedException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnexpectedCourseThemeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedCourseThemeException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HomeworkNotCompletedException.class)
    public ResponseEntity<ErrorResponse> handleHomeworkNotCompletedException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CourseMarkNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseMarkNotFoundException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleStatusRuntimeException(StatusRuntimeException e) {
        HttpStatus httpStatus = HttpStatus.valueOf(e.getStatus().getCode().name());
        return new ResponseEntity<>(new ErrorResponse(httpStatus.getReasonPhrase(), e.getStatus().getDescription()), httpStatus);
    }

    @ExceptionHandler(PlanetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanetNotFoundException(Exception e) {
        logWarning(e);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
