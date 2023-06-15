package org.example.exception.orbitEX;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Ошибка удаления, повторите попытку или обратитесь за помощь к капитану коробля")
public class OrbitDeleteException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public OrbitDeleteException() {
        super("Ошибка удаления орбиты");
    }
}
