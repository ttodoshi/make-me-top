package org.example.feedback.exception.classes.explorer;

public class ExplorerNotStudyingWithKeeperException extends RuntimeException {
    public ExplorerNotStudyingWithKeeperException(Integer explorerId, Integer keeperId) {
        super("Исследователь " + explorerId + " не учится у хранителя " + keeperId);
    }
}
