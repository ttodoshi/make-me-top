package org.example.courseregistration.exception.classes.request;

public class RequestAlreadySentException extends RuntimeException {
    public RequestAlreadySentException() {
        super("Уже существует активный запрос");
    }
}
