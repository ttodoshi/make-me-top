package org.example.exception.classes.requestEX;

public class RequestNotDeniedException extends RuntimeException {
    public RequestNotDeniedException() {
        super("Данный запрос не был закрыт с отказом");
    }
}
