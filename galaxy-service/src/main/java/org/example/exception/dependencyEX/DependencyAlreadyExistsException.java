package org.example.exception.dependencyEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "DependencyAlreadyExistsException")
public class DependencyAlreadyExistsException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public DependencyAlreadyExistsException() {

    }
}
