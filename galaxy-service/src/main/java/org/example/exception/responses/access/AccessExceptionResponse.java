package org.example.exception.responses.access;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AccessExceptionResponse extends ErrorResponse {
    public AccessExceptionResponse() {
        super(HttpStatus.FORBIDDEN, "Вам закрыт доступ к данной функциональности бортового компьютера");
    }
}
