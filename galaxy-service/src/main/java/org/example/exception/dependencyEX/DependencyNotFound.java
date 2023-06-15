package org.example.exception.dependencyEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Навигатор не знает такого пути меджду системами")
public class DependencyNotFound extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public DependencyNotFound() {
        super("Зависимость не найдена");
    }
}
