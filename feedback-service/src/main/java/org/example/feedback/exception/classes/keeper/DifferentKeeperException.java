package org.example.feedback.exception.classes.keeper;

public class DifferentKeeperException extends RuntimeException {
    public DifferentKeeperException() {
        super("У вас нет прав, чтобы сделать это");
    }
}

