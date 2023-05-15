package org.example.exception.systemEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Бортовой компьютер не может найти ифнормацию об этой системе")
public class SystemNotFoundException extends RuntimeException{

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public SystemNotFoundException() {

    }
}
