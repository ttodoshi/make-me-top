package org.example.exception.connecntExceprion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY, reason = "Бортовой компьютер не смог связать с внутренней системой данных")
public class ConnectException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public ConnectException() {

    }
}
