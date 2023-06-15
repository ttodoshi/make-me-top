package org.example.exception.orbitEX;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Бортовой компьютер не может найти информацию об этой орбите")
public class OrbitNotFoundException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public OrbitNotFoundException() {
        super("Орбита не найдена");
    }
}
