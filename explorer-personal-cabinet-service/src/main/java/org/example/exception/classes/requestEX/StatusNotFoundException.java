package org.example.exception.classes.requestEX;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException(Object status) {
        super("Статус запроса " + status + " не обнаружен");
    }
}
