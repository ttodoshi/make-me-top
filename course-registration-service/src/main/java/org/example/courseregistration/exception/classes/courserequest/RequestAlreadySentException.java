package org.example.courseregistration.exception.classes.courserequest;

public class RequestAlreadySentException extends RuntimeException {
    public RequestAlreadySentException() {
        super("Уже существует активный запрос");
    }
}
