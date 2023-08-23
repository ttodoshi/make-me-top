package org.example.exception.classes.requestEX;

public class RequestNotRejectedException extends RuntimeException {
    public RequestNotRejectedException(Integer requestId) {
        super("Запрос " + requestId + " не был закрыт с отказом");
    }
}
