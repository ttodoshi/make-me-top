package org.example.courseregistration.exception.classes.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long requestId) {
        super("Запрос " + requestId + " не найден");
    }

    public RequestNotFoundException() {
        super("Запрос не найден");
    }
}
