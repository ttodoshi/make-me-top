package org.example.courseregistration.exception.classes.request;

public class KeeperRejectionAlreadyExistsException extends RuntimeException {
    public KeeperRejectionAlreadyExistsException() {
        super("По этому запросу уже был отправлен отказ");
    }
}

