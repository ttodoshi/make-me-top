package org.example.feedback.exception.classes.explorer;

public class ExplorerNotStudyingWithKeeperException extends RuntimeException {
    public ExplorerNotStudyingWithKeeperException(Long explorerId, Long keeperId) {
        super("Исследователь " + explorerId + " не учится у хранителя " + keeperId);
    }
}
