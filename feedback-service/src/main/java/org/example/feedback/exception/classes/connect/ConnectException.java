package org.example.feedback.exception.classes.connect;

public class ConnectException extends RuntimeException {
    public ConnectException() {
        super("Бортовой компьютер не смог связаться с внутренней системой данных");
    }
}
