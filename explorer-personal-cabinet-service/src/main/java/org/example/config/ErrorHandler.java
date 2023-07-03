package org.example.config;

import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.progressEX.ProgressDecreaseException;
import org.example.exception.classes.progressEX.SystemParentsNotCompletedException;
import org.example.exception.classes.progressEX.UpdateProgressException;
import org.example.exception.responses.ErrorResponse;
import org.example.exception.responses.connect.ConnectExceptionResponse;
import org.example.exception.responses.progress.ProgressDecreaseExceptionResponse;
import org.example.exception.responses.progress.SystemParentsNotCompletedExceptionResponse;
import org.example.exception.responses.progress.UpdateProgressExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ProgressDecreaseException.class)
    public ResponseEntity<ErrorResponse> handleProgressDecreaseException(ProgressDecreaseException e) {
        return ResponseEntity.ok(new ProgressDecreaseExceptionResponse(e));
    }

    @ExceptionHandler(SystemParentsNotCompletedException.class)
    public ResponseEntity<ErrorResponse> handleSystemParentsNotCompletedException(SystemParentsNotCompletedException e) {
        return ResponseEntity.ok(new SystemParentsNotCompletedExceptionResponse(e));
    }

    @ExceptionHandler(UpdateProgressException.class)
    public ResponseEntity<ErrorResponse> handleUpdateProgressException(UpdateProgressException e) {
        return ResponseEntity.ok(new UpdateProgressExceptionResponse(e));
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ErrorResponse> handleConnectException(ConnectException e) {
        return ResponseEntity.ok(new ConnectExceptionResponse(e));
    }
}
