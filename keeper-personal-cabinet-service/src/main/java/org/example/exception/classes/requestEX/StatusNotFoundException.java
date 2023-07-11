package org.example.exception.classes.requestEX;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException() {
        super("Статус запроса не обнаружен");
    }
}
