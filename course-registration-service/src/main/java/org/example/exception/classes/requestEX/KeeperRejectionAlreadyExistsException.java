package org.example.exception.classes.requestEX;

public class KeeperRejectionAlreadyExistsException extends RuntimeException {
    public KeeperRejectionAlreadyExistsException() {
        super("По этому запросу уже был отправлен отказ");
    }
}

