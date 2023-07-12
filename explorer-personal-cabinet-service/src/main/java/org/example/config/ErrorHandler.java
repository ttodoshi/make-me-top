package org.example.config;

import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.exception.classes.progressEX.PlanetAlreadyCompletedException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.progressEX.UnexpectedProgressValueException;
import org.example.exception.classes.progressEX.UpdateProgressException;
import org.example.exception.classes.requestEX.PersonIsKeeperException;
import org.example.exception.classes.requestEX.PersonIsStudyingException;
import org.example.exception.classes.requestEX.StatusNotFoundException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.access.AccessExceptionResponse;
import org.example.exception.responses.connect.ConnectExceptionResponse;
import org.example.exception.responses.course.CourseNotFoundExceptionResponse;
import org.example.exception.responses.keeper.KeeperNotFoundExceptionResponse;
import org.example.exception.responses.person.PersonNotFoundExceptionResponse;
import org.example.exception.responses.progress.PlanetAlreadyCompletedExceptionResponse;
import org.example.exception.responses.progress.SystemParentsNotCompletedExceptionResponse;
import org.example.exception.responses.progress.UnexpectedProgressValueExceptionResponse;
import org.example.exception.responses.progress.UpdateProgressExceptionResponse;
import org.example.exception.responses.request.PersonIsKeeperExceptionResponse;
import org.example.exception.responses.request.PersonIsStudyingExceptionResponse;
import org.example.exception.responses.request.StatusNotFoundExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(new AccessExceptionResponse(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnexpectedProgressValueException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedProgressValueException(UnexpectedProgressValueException e) {
        return new ResponseEntity<>(new UnexpectedProgressValueExceptionResponse(e), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(PlanetAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handlePlanetAlreadyCompletedException(PlanetAlreadyCompletedException e) {
        return new ResponseEntity<>(new PlanetAlreadyCompletedExceptionResponse(e), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(PersonIsKeeperException.class)
    public ResponseEntity<ErrorResponse> handlePersonIsKeeperException(PersonIsKeeperException e) {
        return new ResponseEntity<>(new PersonIsKeeperExceptionResponse(e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PersonIsStudyingException.class)
    public ResponseEntity<ErrorResponse> handlePersonIsStudyingException(PersonIsStudyingException e) {
        return new ResponseEntity<>(new PersonIsStudyingExceptionResponse(e), HttpStatus.FORBIDDEN);
    }
}
