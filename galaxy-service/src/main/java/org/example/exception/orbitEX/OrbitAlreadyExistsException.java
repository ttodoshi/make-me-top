package org.example.exception.orbitEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "В бортовом компьютере уже информация об этой орбите")
public class OrbitAlreadyExistsException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public OrbitAlreadyExistsException() {
        super("Орбита уже существует");
    }
}
