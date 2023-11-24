package org.example.homework.exception.classes.request;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException(Object status) {
        super("Статус " + status + " не обнаружен");
    }
}
