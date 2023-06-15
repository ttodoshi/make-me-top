package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Бортовой компьютер не смог вас идентифицировать")
public class UserNotFoundException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public UserNotFoundException() {
        super("Пользователь не найден");
    }
}
