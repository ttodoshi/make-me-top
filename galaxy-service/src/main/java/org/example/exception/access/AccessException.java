package org.example.exception.access;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Вам закрыт доступ к данной функциональности бортового компьютера")
public class AccessException extends AccessDeniedException {
    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

    public AccessException() {
        super("Вам закрыт доступ к данной функциональности бортового компьютера");
    }
}
