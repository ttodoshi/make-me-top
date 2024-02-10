package org.example.person.exception.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long requestId) {
        super("Запрос " + requestId + " не найден");
    }
}
