package org.example.progress.exception.keeper;

public class DifferentKeeperException extends RuntimeException {
    public DifferentKeeperException() {
        super("У вас нет прав, чтобы сделать это");
    }
}
