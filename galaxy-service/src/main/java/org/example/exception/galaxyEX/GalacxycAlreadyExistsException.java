package org.example.exception.galaxyEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "В бортовом компьютере уже есть информация об этой галактике")
public class GalacxycAlreadyExistsException extends RuntimeException {
    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

    public GalacxycAlreadyExistsException() {

    }
}
