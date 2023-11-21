package org.example.courseregistration.exception.classes.request;

public class RequestNotRejectedException extends RuntimeException {
    public RequestNotRejectedException(Integer requestId) {
        super("Запрос " + requestId + " не был закрыт с отказом");
    }
}

