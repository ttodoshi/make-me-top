package org.example.exception.classes.requestEX;

public class RequestAlreadySentException extends RuntimeException {
    public RequestAlreadySentException() {
        super("Уже существует активный запрос");
    }
}
