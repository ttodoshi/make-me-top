package org.example.courseregistration.exception.courserequest;

public class RequestAlreadySentException extends RuntimeException {
    public RequestAlreadySentException() {
        super("Уже существует активный запрос");
    }
}
