package org.example.exception.systemEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "В бортовом компьютере уже есть информация об этой системе")
public class SystemAlreadyExistsException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public SystemAlreadyExistsException() {
        super("Система уже существует");
    }
}
