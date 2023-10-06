package org.example.exception.classes.requestEX;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Integer requestId) {
        super("Запрос " + requestId + " не найден");
    }

    public RequestNotFoundException() {
        super("Запрос не найден");
    }
}
