package org.example.exception.galaxyEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Бортовой компьютер не может найти эту галактику")
public class GalacxyNotFoundException extends EntityNotFoundException {
    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

    public GalacxyNotFoundException() {

    }
}

