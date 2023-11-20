package org.example.homework.exception.classes.keeper;

public class DifferentKeeperException extends RuntimeException {
    public DifferentKeeperException() {
        super("У вас нет прав, чтобы сделать это");
    }
}
