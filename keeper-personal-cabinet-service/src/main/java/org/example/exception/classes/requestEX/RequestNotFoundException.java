package org.example.exception.classes.requestEX;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Integer requestId) {
        super("Бортовой компьютер не смог найти информацию о запросе " + requestId);
    }
}
