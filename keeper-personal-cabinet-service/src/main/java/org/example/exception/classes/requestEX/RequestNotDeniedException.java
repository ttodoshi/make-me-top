package org.example.exception.classes.requestEX;

public class RequestNotDeniedException extends RuntimeException {
    public RequestNotDeniedException(Integer requestId) {
        super("Запрос " + requestId + " не был закрыт с отказом");
    }
}
