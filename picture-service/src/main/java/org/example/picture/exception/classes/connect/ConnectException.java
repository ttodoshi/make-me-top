package org.example.picture.exception.classes.connect;

public class ConnectException extends RuntimeException {
    public ConnectException() {
        super("Бортовой компьютер не смог связаться с внутренней системой данных");
    }
}
