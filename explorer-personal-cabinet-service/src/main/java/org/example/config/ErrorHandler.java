package org.example.config;

import org.example.exception.classes.accessEX.AccessException;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.exception.classes.progressEX.ProgressDecreaseException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.progressEX.UpdateProgressException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.access.AccessExceptionResponse;
import org.example.exception.responses.connect.ConnectExceptionResponse;
import org.example.exception.responses.course.CourseNotFoundExceptionResponse;
import org.example.exception.responses.keeper.KeeperNotFoundExceptionResponse;
import org.example.exception.responses.person.PersonNotFoundExceptionResponse;
import org.example.exception.responses.progress.ProgressDecreaseExceptionResponse;
import org.example.exception.responses.progress.SystemParentsNotCompletedExceptionResponse;
import org.example.exception.responses.progress.UpdateProgressExceptionResponse;
import org.example.exception.responses.request.StatusNotFoundExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ErrorResponse> handleAccessException(AccessException e) {
        return new ResponseEntity<>(new AccessExceptionResponse(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(new AccessExceptionResponse(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
        return new ResponseEntity<>(new AccessExceptionResponse(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ProgressDecreaseException.class)
    public ResponseEntity<ErrorResponse> handleProgressDecreaseException(ProgressDecreaseException e) {
        return new ResponseEntity<>(new ProgressDecreaseExceptionResponse(e), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(SystemParentsNotCompletedException.class)
    public ResponseEntity<ErrorResponse> handleSystemParentsNotCompletedException(SystemParentsNotCompletedException e) {
        return new ResponseEntity<>(new SystemParentsNotCompletedExceptionResponse(e), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(UpdateProgressException.class)
    public ResponseEntity<ErrorResponse> handleUpdateProgressException(UpdateProgressException e) {
        return new ResponseEntity<>(new UpdateProgressExceptionResponse(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponse> handleConnectException(ConnectException e) {
        return new ResponseEntity<>(new ConnectExceptionResponse(e), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonNotFoundException(PersonNotFoundException e) {
        return new ResponseEntity<>(new PersonNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KeeperNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleKeeperNotFoundException(KeeperNotFoundException e) {
        return new ResponseEntity<>(new KeeperNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFoundException(CourseNotFoundException e) {
        return new ResponseEntity<>(new CourseNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatusNotFoundException(StatusNotFoundException e) {
        return new ResponseEntity<>(new StatusNotFoundExceptionResponse(e), HttpStatus.NOT_FOUND);
    }
}
