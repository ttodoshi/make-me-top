package org.example.exception.classes.requestEX;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException() {
        super("Бортовой компьютер не смог найти информацию о данном запросе");
    }
}
