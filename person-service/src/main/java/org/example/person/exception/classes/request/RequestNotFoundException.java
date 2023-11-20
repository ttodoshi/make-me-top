package org.example.person.exception.classes.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Integer requestId) {
        super("Запрос " + requestId + " не найден");
    }
}
