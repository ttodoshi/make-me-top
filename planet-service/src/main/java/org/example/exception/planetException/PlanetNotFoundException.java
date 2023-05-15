package org.example.exception.planetException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Бортовой компьютер не может найти эту  планету")
public class PlanetNotFoundException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public PlanetNotFoundException() {

    }
}
