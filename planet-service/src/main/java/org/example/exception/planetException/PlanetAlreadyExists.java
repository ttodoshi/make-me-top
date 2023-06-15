package org.example.exception.planetException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "В бортовом компьютере уже информация об этой планете")
public class PlanetAlreadyExists extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public PlanetAlreadyExists() {
        super("Планета уже существует");
    }
}
