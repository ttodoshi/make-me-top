package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Бортовой компьютер не может найти информацию об этой системе")
public class SystemNotFoundException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public SystemNotFoundException() {
        super("Система не найдена");
    }
}
