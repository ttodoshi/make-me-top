package org.example.exception.responses.request;

import org.example.exception.responses.ErrorResponse;
import org.springframework.http.HttpStatus;

public class RequestNotFoundExceptionResponse extends ErrorResponse {
    public RequestNotFoundExceptionResponse() {
        super(HttpStatus.NOT_FOUND, "Бортовой компьютер не смог найти информацию о данном запросе");
    }
}
