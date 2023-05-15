package org.example.exception.orbitEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "По заданым координатам в навигаторе уже хранится объект")
public class OrbitCoordinatesException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public OrbitCoordinatesException() {

    }
}
