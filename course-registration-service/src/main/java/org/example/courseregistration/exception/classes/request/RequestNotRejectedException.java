package org.example.courseregistration.exception.classes.request;

public class RequestNotRejectedException extends RuntimeException {
    public RequestNotRejectedException(Long requestId) {
        super("Запрос " + requestId + " не был закрыт с отказом");
    }
}

